package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Network.Message.Info.PingInfo;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.UserAction.PingUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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

    private boolean active = true;

    public SocketConnection(Socket clientSocket, Server server) {
        this.socket = clientSocket;
        this.server = server;
        try {
            socket.setSoTimeout(2000); //2 seconds time out, client needs to keep heartbeat
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
     * Method for receiving an action from the client socket, that forwards it to the server that
     * owns this connection thread.
     */
    public void receiveAction() {
        UserAction action;
        try {
            Object message = inputStream.readObject();
            action = (UserAction) message;
            if(action instanceof PingUserAction)
                sendMessage(new PingInfo());
            else
                server.parseAction(this, action);
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            if(server instanceof MatchServer)
                closeMatch();
            else
                close();
        }
    }

    /**
     * Method for sending a message to the client socket.
     *
     * @param message the Message object to send
     */
    public synchronized void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset(); //FIXME: !!!!!!!! ERRORS!!!!!!!!!! maybe fixed with synchronizing
        } catch (Exception e) { // what exceptions are thrown here?
            e.printStackTrace();
            if(server instanceof MatchServer)
                closeMatch();
            else
                close();
        }
    }
}
