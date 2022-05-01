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

/**
 * This class is the first to be created server-side, and it in turn creates a server socket which
 * accepts all incoming connections. The connected clients are then logged into existing matches or
 * into new ones, both created and managed by the LobbyServer.
 */
public class LobbyServer implements Server {

    private boolean active = true;

    private Map<InetAddress, String> registeredNicks = new HashMap<>();

    private InetAddress IP;

    private int mainPort = 4646;

    private int maxPort = mainPort + 1;

    private ServerSocket mainSocket;

    private Set<MatchServer> matchServers = new HashSet<>();

    private Set<ConnectionThread> connections = new HashSet<>();

    /**
     * Constructor for a LobbyServer object. The constructor will create a first socket to which all
     * clients connect first, to later establish the server they will be bound to.
     */
    public LobbyServer() {
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
                ConnectionThread newConnection = new ConnectionThread(tempSocket, this);
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
        for (ConnectionThread connection : connections) {
            connection.close();
        }
        for (MatchServer server : matchServers) {
            server.close();
            matchServers.remove(server);
        }
    }

    public int createMatchPort() {
        int tmp = maxPort;
        maxPort++;
        return tmp;
    }

    @Override
    public void parseAction(ConnectionThread connectionThread, UserAction userAction) {
        if (userAction.getUserActionType() == UserActionType.LOGIN) {
            handleLogin(connectionThread, (LoginUserAction) userAction);
        }
        else {
            connectionThread.sendMessage(new LoginError("Sending user action to a lobby server; log in first!"));
        }
    }

    public void handleLogin(ConnectionThread connectionThread, LoginUserAction loginAction) {
        if (registeredNicks.containsValue(loginAction.getNickname())) {
            connectionThread.sendMessage(new LoginError("Nickname already taken. Choose another one!"));
        }
        else {
            MatchServer serverToConnect = null;
            for (MatchServer server : matchServers) {
                if (server.isInitialized() && !server.isFull()) {
                    serverToConnect = server;
                    break;
                }
            }
            if (serverToConnect == null) {
                serverToConnect = new MatchServer(this, createMatchPort());
                matchServers.add(serverToConnect);
            }
            connectionThread.sendMessage(new ServerLoginInfo(IP, serverToConnect.getPort()));
            connectionThread.close();
            connections.remove(connectionThread);
            serverToConnect.await(connectionThread.getInetAddress(), loginAction.getNickname());
        }
    }

    public void registerClient(InetAddress IP, String nickname) {
        registeredNicks.put(IP, nickname);
    }

    public void unregisterClient(InetAddress IP, String nickname) {
        registeredNicks.remove(IP, nickname);
    }
}
