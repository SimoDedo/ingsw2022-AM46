package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Network.Message.Error.DisconnectionError;
import it.polimi.ingsw.Network.Message.Info.LogoutSuccessfulInfo;
import it.polimi.ingsw.Network.Message.Info.PingInfo;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.UserAction.LogoutUserAction;
import it.polimi.ingsw.Network.Message.UserAction.PingUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that manages a connection from a client. All messages will be forwarded to the server which
 * owns this connection, which also has a reference to the server socket coupled with the client socket
 * in this class.
 */
public class SocketConnection implements Runnable {

    protected final Socket socket;

    private final Server server;

    private ObjectInputStream inputStream;

    private ObjectOutputStream outputStream;

    private final ExecutorService serverAction;

    private String nickname;

    private boolean active = true;
    private boolean readyToDisconnect = false;
    private boolean loggedIn;

    public SocketConnection(Socket clientSocket, Server server) {
        loggedIn = true;
        serverAction = Executors.newSingleThreadExecutor();
        this.socket = clientSocket;
        this.server = server;
        try {
            socket.setSoTimeout(5000); //2 seconds time out, client needs to keep heartbeat
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            System.err.println("Error in initialization of connection thread: ");
            ioe.printStackTrace();
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    /**
     * Setter for the active boolean.
     *
     * @param active the new value of active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for the active boolean.
     *
     * @return true if the connection is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method for creating an independent thread linked to a ConnectionThread object, which is tasked
     * with accepting incoming messages (and dealing with them by forwarding them to the server).
     */
    @Override
    public void run() {
        while (isActive()) {
            receiveAction();
        }
    }

    private void handleLogout(){
        server.handleLogout(nickname);
        loggedIn = false;
        sendMessage(new LogoutSuccessfulInfo());
        setActive(false);
    }

    /**
     * Handles the disconnection of the user from this connection socket.
     */
    private void handleDisconnection(){
        if(server instanceof MatchServer && loggedIn){
            System.err.println("\"" + nickname + "\" (" + this.getInetAddress() +") unexpectedly disconnected.");
            close();
            ((MatchServer) server).sendAll(new DisconnectionError(nickname + " disconnected!"));
            closeMatch();
        }
        else
            close();
    }

    public void close() {
        setActive(false);
        try {
            socket.close();
        } catch (IOException ioe) {
            System.err.println("Error while closing socket connection: ");
            ioe.printStackTrace();
        }
    }

    public void closeMatch(){
        if(server instanceof MatchServer) {
            server.close(); //If one disconnects, all disconnected from match
        }
    }

    /**
     * Prepares the connection to handle the disconnection of a client.
     * This means stopping any thread from writing to the socket since it will shortly be closed.
     */
    public synchronized void readyDisconnect(){
        readyToDisconnect = true;
    }

    /**
     * Method for receiving an action from the client socket, that forwards it to the server that
     * owns this connection thread.
     */
    private void receiveAction() {
        UserAction action;
        try {
            Object message = inputStream.readObject();
            action = (UserAction) message;
            serverAction.execute(()->{
                if(action instanceof PingUserAction){
                    if(isActive())
                        sendMessage(new PingInfo());
                }
                else if(action instanceof LogoutUserAction)
                    handleLogout();
                else{
                    try{
                        server.parseAction(this, action);
                    }
                    catch (Throwable t){
                        t.printStackTrace();
                        this.close();
                    }
                }
            });
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            if(isActive())
                handleDisconnection();
        }
    }

    /**
     * Method for sending a message to the client socket.
     *
     * @param message the Message object to send
     */
    public synchronized void sendMessage(Message message) {
        if(isActive() && ! readyToDisconnect){
            try {
                outputStream.writeObject(message);
                outputStream.flush();
                outputStream.reset();
            } catch (Exception e) { // what exceptions are thrown here?
                e.printStackTrace();
                System.out.println("Unable to write :" + message.getSender() + " " + message.getClass());
                handleDisconnection();
            }
        }
    }
}
