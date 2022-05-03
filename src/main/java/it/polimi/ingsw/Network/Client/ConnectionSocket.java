package it.polimi.ingsw.Network.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Setups initial socket connection with server
 */
public class ConnectionSocket {

    private final String serverAddress;
    private final int serverPort;
    private ObjectOutputStream outputStream;

    public ConnectionSocket(String serverAddress, int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean setup(String nickname){
        Socket socket;
        try {
            socket = new Socket(serverAddress, serverPort);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e){
            return false;
        }
        return true;
    }
}
