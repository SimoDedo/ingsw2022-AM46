package it.polimi.ingsw.Network.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Setups initial socket connection with server
 */
public class ConnectionSocket {

    private final String serverAddress;
    private final int serverPort;
    private ObjectOutputStream outputStream;
    private ListenerSocket listener;

    public ConnectionSocket(String serverAddress, int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean setup(String nickname, ActionHandler actionHandler){
        Socket socket;
        try {
            socket = new Socket(serverAddress, serverPort);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            listener = new ListenerSocket(socket, actionHandler, input);

            Thread thread = new Thread(listener);
            thread.start();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    private boolean nicknameChecker(){return false;}
}
