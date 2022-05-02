package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.UserAction.LoginUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;
import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class models an instance of a server, which is tasked with communicating with the players
 * through the VirtualView class.
 */
public class MatchServer implements Server, Runnable {

    private LobbyServer lobbyServer;

    private Controller controller;

    private boolean active = true;

    /**
     * The port on which the server socket of this MatchServer will listen.
     */
    private int port;

    private ServerSocket serverSocket;

    /**
     * HashMap for storing the future client connections which will be accepted on this server.
     */
    private Map<InetAddress, String> awaitingMap = new HashMap<>();

    /**
     * HashMap for storing the connections on the server associated with each nickname.
     */
    private Map<String, ConnectionThread> connectionMap = new HashMap<>();

    /**
     * HashMap for storing the VirtualViews associated with each nickname.
     */
    private Map<String, VirtualView> viewMap = new HashMap<>();

    private ExecutorService executor;

    /**
     * Constructor for the MatchServer class.
     */
    public MatchServer(LobbyServer lobbyServer, int port) {
        this.lobbyServer = lobbyServer;
        this.controller = new Controller(this);
        this.port = port;
        executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Match Server now listening on port " + port);
        } catch (IOException ioe) {
            System.err.println("Match server socket binding operation failed: ");
            ioe.printStackTrace();
            close();
        }
        while (isActive()) {
            Socket tempSocket;
            try {
                tempSocket = serverSocket.accept();
                ConnectionThread newConnection = new ConnectionThread(tempSocket, this);
                executor.execute(newConnection);
            } catch (IOException ioe) {
                System.err.println("IO error while accepting connection: ");
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Getter for the full boolean.
     *
     * @return true if the server is full (i.e. the controller has the maximum number of players),
     * false otherwise
     */
    public boolean isFull() {
        return controller.isFull();
    }

    /**
     * Getter for the initialized boolean.
     *
     * @return true if the server is initialized (i.e. the game is present), false otherwise
     */
    public boolean isInitialized() {
        return controller.isInitialized();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPort() {
        return port;
    }

    public void close() {
        setActive(false);
        for (ConnectionThread connection : connectionMap.values()) {
            connection.close();
        }
        lobbyServer.deleteMatch(this);
    }

    /**
     * Method that adds a Map.Entry<InetAddress, String> to this server, containing the IP and nickname
     * of a client that is trying to connect (and has the right to) to the server. The server will
     * refuse all incoming connections that it wasn't "awaiting", since it only allows connections from
     * clients that have previously connected to the lobby server.
     *
     * @param IP the IP of the client that connected to the lobby server
     * @param nickname the nickname provided to the lobby server
     */
    public void await(InetAddress IP, String nickname) {
        awaitingMap.put(IP, nickname);
    }

    public void sendMessage(String nickname, Message message) {
        ConnectionThread connectionThread = connectionMap.get(nickname);
        connectionThread.sendMessage(message);
    }

    public void sendAll(Message message) {
        for (ConnectionThread connectionThread : connectionMap.values()) {
            connectionThread.sendMessage(message);
        }
    }

    public void sendAllExcept(String nickname, Message message) {
        for (Map.Entry<String, ConnectionThread> entry : connectionMap.entrySet()) {
            if (!entry.getKey().equals(nickname)) entry.getValue().sendMessage(message);
        }
    }

    @Override
    public void parseAction(ConnectionThread connectionThread, UserAction userAction) {
        if (userAction.getUserActionType() == UserActionType.LOGIN) {
            handleLogin(connectionThread, (LoginUserAction) userAction);
        } else {
            controller.receiveUserAction(userAction);
        }
    }

    public void handleLogin(ConnectionThread connectionThread, LoginUserAction loginAction) {
        InetAddress IP = connectionThread.getInetAddress();
        String nickname = loginAction.getNickname();
        if (awaitingMap.containsKey(IP) && awaitingMap.get(IP).equals(nickname)) {
            if (!isFull()) {
                registerClient(IP, nickname, connectionThread);
                controller.loginHandle(nickname);
                viewMap.put(nickname, new VirtualView());
            } else {
                connectionThread.sendMessage(new LoginError("The server is full!"));
                connectionThread.close();
            }

        } else if (connectionMap.containsKey(nickname) && connectionMap.get(nickname).equals(connectionThread)) {
            connectionThread.sendMessage(new LoginError("User already logged in!"));
        }
        else {
            connectionThread.sendMessage(new LoginError("User didn't connect to the lobby first!"));
            connectionThread.close();
        }
    }

    public void registerClient(InetAddress IP, String nickname, ConnectionThread connectionThread) {
        connectionMap.put(nickname, connectionThread);
        lobbyServer.registerClient(IP, nickname);
    }

    public void unregisterClient(InetAddress IP, String nickname, ConnectionThread connectionThread) {
        connectionMap.remove(nickname, connectionThread);
        lobbyServer.unregisterClient(IP, nickname);
    }
}
