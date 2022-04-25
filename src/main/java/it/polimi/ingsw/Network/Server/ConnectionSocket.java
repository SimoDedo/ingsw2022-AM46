package it.polimi.ingsw.Network.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Class to bind server and client socket
 */
public class ConnectionSocket implements Runnable{

    ExecutorService executor;
    ServerSocket serverSocket;
    int port;
    int clientID;
    boolean active;


    /**
     * @param port number passed on by the server
     */
    public ConnectionSocket(int port){
        this.port = port;
        this.clientID = 0;
        this.active = true; // non so
    }


    /**
     * @param active if the ConnectionSocket is markes as inactive the server will stop listening for / accepting
     *               new connections
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Handles the creation of new sockets if a client requests one. Listens until this.active is set to false
     */
    @Override
    public void run(){
        try{
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        while(this.active){
            try {
                Socket connection = serverSocket.accept();
                executor.submit(new VirtualView(connection, clientID));
                clientID++;
            } catch (IOException e){
                System.err.println(e.getMessage());
                break;
            }
        }
        executor.shutdown();
    }


}
