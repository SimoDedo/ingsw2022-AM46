package it.polimi.ingsw.Network.Message.Info;

/**
 * Server response to a Ping UserAction.
 * It contains no valuable info, it is sent only to avoid the client socket timeout to expire.
 */
public class PingInfo extends Info{
    public PingInfo() {
        super("pong");
    }
}
