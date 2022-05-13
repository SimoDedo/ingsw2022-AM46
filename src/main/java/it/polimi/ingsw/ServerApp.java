package it.polimi.ingsw;

import it.polimi.ingsw.Network.Server.LobbyServer;

public class ServerApp {
    public static void main(String[] args){
        LobbyServer server = new LobbyServer();
        server.startServer();
    }
}
