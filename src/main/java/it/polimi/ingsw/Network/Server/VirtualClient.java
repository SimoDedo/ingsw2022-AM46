package it.polimi.ingsw.Network.Server;

import java.net.Socket;

public class VirtualClient implements Runnable{
    Socket socket;
    int id;

    public VirtualClient(Socket socket, int id){
        this.socket = socket;
        this.id = id;
    }

    public void run(){
        System.out.print("WOW");
    }
}
