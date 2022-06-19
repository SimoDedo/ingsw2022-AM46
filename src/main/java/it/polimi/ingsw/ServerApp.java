package it.polimi.ingsw;

import it.polimi.ingsw.Network.Server.LobbyServer;

/**
 * Application used to start a server. It is called by App and not directly started when using a jar.
 * To see possible parameters, look at @see {@link it.polimi.ingsw.App}
 */
public class ServerApp {

    public static void main(String[] args){
        LobbyServer server = new LobbyServer();
        server.startServer();
    }
}
