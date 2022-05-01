package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Class that manages a connection from a client. All messages will be forwarded to the server which
 * owns this connection, which also has a reference to the server socket coupled with the client socket
 * in this class.
 */
public class ConnectionThread implements Runnable {

    protected Socket socket;

    private Server server;

    // private ExecutorService executorService;

    private ObjectInputStream inputStream;

    private ObjectOutputStream outputStream;

    private boolean active;

    public ConnectionThread(Socket clientSocket, Server server) {
        this.socket = clientSocket;
        this.server = server;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            System.err.println("Error in initialization of connection thread: ");
            ioe.printStackTrace();
        }
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

    public void close() {
        setActive(false);
        try {
            socket.close();
        } catch (IOException ioe) {
            System.err.println("Error while closing connection thread: ");
            ioe.printStackTrace();
        }
    }

    /**
     * Method for receiving an action from the client socket, that forwards it to the server that
     * owns this connection thread.
     */
    public void receiveAction() {
        UserAction action;
        try {
            Object message = inputStream.readObject();
            action = (UserAction) message;
            server.parseAction(this, action);
        } catch (Exception e) { // what exceptions are thrown here?
            e.printStackTrace();
        }
    }

    /**
     * Method for sending a message to the client socket.
     *
     * @param message the Message object to send
     */
    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) { // what exceptions are thrown here?
            e.printStackTrace();
        }
    }
}
