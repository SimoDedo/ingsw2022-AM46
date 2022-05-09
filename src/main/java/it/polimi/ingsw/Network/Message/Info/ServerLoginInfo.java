package it.polimi.ingsw.Network.Message.Info;

import java.net.InetAddress;

public class ServerLoginInfo extends Info {

    private InetAddress IP;
    private int port;

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

