package it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * WIP
 */
public class ServerHandler {

    private Set<String> currentlyUsedNicks = new HashSet<>();

    private int mainPort = 4646;

    private ServerSocket mainSocket;

    /**
     * Constructor for a ServerHandler object. The constructor will create a first socket to which all
     * clients connect first, to later establish the server they will be bound to.
     */
    public ServerHandler() {
        try {
            mainSocket = new ServerSocket(mainPort);
        }
        catch (IOException ioe) {
            System.err.println("Main socket binding operation failed:");
            ioe.printStackTrace();
            System.exit(-1);
        }
    }

    public void createServer() {

    }
}
