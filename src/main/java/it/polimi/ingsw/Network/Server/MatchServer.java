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

    private final LobbyServer lobbyServer;

    private final Controller controller;

    private boolean active = true;

    /**
     * The port on which the server socket of this MatchServer will listen.
     */
    private final int port;

    private ServerSocket serverSocket;

    /**
     * HashMap for storing the future client connections which will be accepted on this server.
     */
    private final Map<String, InetAddress> awaitingMap = new HashMap<>();

    /**
     * HashMap for storing the connections on the server associated with each nickname.
     */
    private final Map<String, SocketConnection> connectionMap = new HashMap<>();

    private final ExecutorService executor;

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
                SocketConnection newConnection = new SocketConnection(tempSocket, this);
                System.out.println(newConnection.getInetAddress() + " connected to match server on port " + port);
                executor.execute(newConnection);
            } catch (IOException ioe) {
                if(isActive()){
                    System.err.println("IO error while accepting connection: ");
                    ioe.printStackTrace();
                }
                else{
                    System.err.println("Closing match server on port " + port);
                }
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

    public synchronized void close() {
        setActive(false);
        HashMap<String, SocketConnection> copy = new HashMap<>(connectionMap);
        for(Map.Entry<String, SocketConnection> entry : copy.entrySet()){
            SocketConnection connection= entry.getValue();
            String nickname = entry.getKey();
            if(connection.isActive()){
                connection.close();
                System.err.println("Match server on port " + port + " closed connection with: \"" + nickname + "\" (" + connection.getInetAddress() +")");
            }
            unregisterClient(nickname);
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error while closing match thread: ");
            e.printStackTrace();
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
    public void await(String nickname, InetAddress IP) {
        awaitingMap.put(nickname, IP);
    }

    public void sendMessage(String nickname, Message message) {
        SocketConnection socketConnection = connectionMap.get(nickname);
        socketConnection.sendMessage(message);
    }

    public void sendAll(Message message) {
        for (SocketConnection socketConnection : connectionMap.values()) {
            socketConnection.sendMessage(message);
        }
    }

    public void sendAllExcept(String nickname, Message message) {
        for (Map.Entry<String, SocketConnection> entry : connectionMap.entrySet()) {
            if (!entry.getKey().equals(nickname)) entry.getValue().sendMessage(message);
        }
    }

    @Override
    public synchronized void parseAction(SocketConnection socketConnection, UserAction userAction) {
        if (userAction.getUserActionType() == UserActionType.LOGIN) {
            handleLogin(socketConnection, (LoginUserAction) userAction);
        } else {
            controller.receiveUserAction(userAction);
        }
    }

    public void handleLogin(SocketConnection socketConnection, LoginUserAction loginAction) {
        InetAddress IP = socketConnection.getInetAddress();
        String nickname = loginAction.getNickname();
        if (awaitingMap.containsKey(nickname) && awaitingMap.containsValue(IP) && awaitingMap.get(nickname).equals(IP)){
            if (!isFull()) {
                System.out.println("\"" + nickname + "\" (" + IP + ") logged in match server on port " + port);
                registerClient(IP, nickname, socketConnection);
                socketConnection.setNickname(nickname);
                controller.loginHandle(nickname);
            } else {
                socketConnection.sendMessage(new LoginError("The server is full!"));
                socketConnection.close();
            }

        } else if (connectionMap.containsKey(nickname) && connectionMap.get(nickname).equals(socketConnection)) {
            socketConnection.sendMessage(new LoginError("User already logged in!"));
        }
        else {
            socketConnection.sendMessage(new LoginError("User didn't connect to the lobby first!"));
            socketConnection.close();
        }
    }

    @Override
    public void handleLogout(String nickname){
        System.out.println("\"" + nickname + "\" (" + connectionMap.get(nickname).getInetAddress() + ") logged out from match server on port " + port);
        unregisterClient(nickname);
        if(connectionMap.isEmpty())
            close();
    }

    public void registerClient(InetAddress IP, String nickname, SocketConnection socketConnection) {
        connectionMap.put(nickname, socketConnection);
        lobbyServer.registerClient(nickname, IP);
    }

    public void unregisterClient(String nickname) {
        connectionMap.remove(nickname);
        lobbyServer.handleLogout(nickname);
    }
}
