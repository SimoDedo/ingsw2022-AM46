package it.polimi.ingsw.Network.Message.Info;

import java.net.InetAddress;

/**
 * Info sent by the LobbyServer to inform the client of the address and port of the MatchServer assigned.
 */
public class ServerLoginInfo extends Info {

    private final InetAddress IP;
    private final int port;

    public ServerLoginInfo(InetAddress IP, int port) {
        super("Please connect to server using address and port: ");
        this.IP = IP;
        this.port = port;
    }

    public InetAddress getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return super.toString() + IP.getHostAddress() + ":" + port;
    }
}

