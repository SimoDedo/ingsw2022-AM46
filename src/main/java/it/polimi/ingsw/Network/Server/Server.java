package it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * This class models an instance of a server, which is tasked with communicating with the players
 * through the VirtualView class.
 */
public class Server {

    private boolean full = false;

    /**
     * HashMap for storing the client IDs associated with each connected virtual view on the server.
     */
    private Map<VirtualView, Integer> viewIDs = new HashMap<>();

    /**
     * Constructor for the Server class.
     */
    public Server(int port) {

    }

    public void registerView() {

    }

    public void singleSend() {

    }

    public void sendAll() {

    }

    public void sendAllExcept() {

    }

    public void parseAction() {

    }

    public void playerDisconnected() {

    }
}
