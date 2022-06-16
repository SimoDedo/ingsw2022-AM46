package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Info.ServerLoginInfo;
import it.polimi.ingsw.Network.Message.UserAction.LoginUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;
import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the first to be created server-side, and it in turn creates a server socket which
 * accepts all incoming connections. The connected clients are then logged into existing matches or
 * into new ones, both created and managed by the LobbyServer.
 */
public class LobbyServer implements Server {

    private boolean active = true;

    private final Map<String, InetAddress> registeredNicks = new HashMap<>();

    private InetAddress IP;

    private final int mainPort = 4646;

    /**
     * A map for storing each usable port and whether it is currently occupied by a match server or not (true = occupied).
     */
    private final Map<Integer, Boolean> matchPorts = new LinkedHashMap<>();

    private ServerSocket mainSocket;

    private final Set<MatchServer> matchServers = new HashSet<>();

    private final Set<SocketConnection> connections = new HashSet<>();

    private final ExecutorService executor;

    /**
     * Constructor for a LobbyServer object. The constructor will create a first socket to which all
     * clients connect first, to later establish the server they will be bound to.
     */
    public LobbyServer() {
        executor = Executors.newCachedThreadPool();
        for (int i = mainPort+1; i < mainPort+100; i++) {
            matchPorts.put(i, false);
        }
    }

    public void startServer(){
        try {
            mainSocket = new ServerSocket(mainPort);
            IP = mainSocket.getInetAddress();
            System.out.println("Server now listening...");
        }
        catch (IOException ioe) {
            System.err.println("Main server socket binding operation failed: ");
            ioe.printStackTrace();
            System.exit(-1);
        }
        while (isActive()) {
            Socket tempSocket;
            try {
                tempSocket = mainSocket.accept();
                SocketConnection newConnection = new SocketConnection(tempSocket, this);
                System.out.println(newConnection.getInetAddress() + " connected to lobby server.");
                executor.execute(newConnection);
                connections.add(newConnection);
            } catch (IOException ioe) {
                System.err.println("IO error: " + ioe);
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Stops the socket, closes all open connections, and instructs all match servers to do the same.
     */
    public void close() {
        setActive(false);
        for (SocketConnection connection : connections) {
            connection.close();
        }
        for (MatchServer server : matchServers) {
            server.close();
            matchServers.remove(server);
        }
    }

    public int getMatchPort() {
        for (Integer port : matchPorts.keySet()) {
            if (!matchPorts.get(port)) return port;
        }
        return 0;
    }

    @Override
    public synchronized void parseAction(SocketConnection socketConnection, UserAction userAction) {
        if (userAction.getUserActionType() == UserActionType.LOGIN) {
            handleLogin(socketConnection, (LoginUserAction) userAction);
        }
        else {
            socketConnection.sendMessage(new LoginError("Sending user action to a lobby server; log in first!"));
        }
    }

    public void handleLogin(SocketConnection socketConnection, LoginUserAction loginAction) {
        int port = getMatchPort();
        if (registeredNicks.containsKey(loginAction.getNickname())) {
            socketConnection.sendMessage(new LoginError("Nickname already taken. Choose another one!"));
        }
        else if (loginAction.getNickname().length() > 15) {
            socketConnection.sendMessage(new LoginError("Nickname too long. Choose a nickname not longer than 15 characters!"));
        }
        else if (loginAction.getNickname() == null || loginAction.getNickname().equals("")) {
            socketConnection.sendMessage(new LoginError("Nickname can't be empty!"));
        }
        else if (!Character.isLetterOrDigit(loginAction.getNickname().charAt(0))) {
            socketConnection.sendMessage(new LoginError("Nickname should start with a letter or number!"));
        }
        else  if (port == 0) {
            socketConnection.sendMessage(new LoginError("The server is currently full. Please retry later!"));
        }
        else {
            System.out.println("\"" + loginAction.getNickname() + "\" (" + socketConnection.getInetAddress() + ") logged in.");
            registerClient(loginAction.getNickname(), socketConnection.getInetAddress());
            socketConnection.setNickname(loginAction.getNickname());
            MatchServer serverToConnect = null;
            for (MatchServer server : matchServers) {
                if (server.isInitialized() && !server.isFull()) {
                    serverToConnect = server;
                    break;
                }
            }
            if (serverToConnect == null) {
                serverToConnect = new MatchServer(this, port);
                matchPorts.replace(port, true);
                executor.execute(serverToConnect);
                matchServers.add(serverToConnect);
            }
            serverToConnect.await(loginAction.getNickname(), socketConnection.getInetAddress());
            socketConnection.sendMessage(new ServerLoginInfo(IP, serverToConnect.getPort()));
            socketConnection.close();
            connections.remove(socketConnection);
        }
    }

    @Override
    public void handleLogout(String nickname) {
        unregisterClient(nickname);
    }


    public void deleteMatch(MatchServer matchServer) {
        matchServers.remove(matchServer);
        matchPorts.replace(matchServer.getPort(), false);
    }

    public void registerClient(String nickname, InetAddress IP) {
        registeredNicks.put(nickname, IP);

    }

    public void unregisterClient(String nickname) {
        System.out.println("\"" + nickname + "\" (" + registeredNicks.get(nickname) + ") logged out from Lobby.");
        registeredNicks.remove(nickname);
    }
}
