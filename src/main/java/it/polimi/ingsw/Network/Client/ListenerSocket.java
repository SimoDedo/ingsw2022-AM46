package it.polimi.ingsw.Network.Client;

import java.net.Socket;

/**
 * Listens for server answers and forwards them to ...
 */
public class ListenerSocket implements Runnable{

    private final Socket socket;

    public ListenerSocket(Socket socket){
        this.socket = socket;
    }

    public void run(){}
}
