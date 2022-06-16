package it.polimi.ingsw.Network.Message.UserAction;

/**
 * User action sent automatically to ping the server.
 * It contains no valuable info and is needed to avoid the socket timeout to expire when no action is being taken.
 */
public class PingUserAction extends UserAction{

    public PingUserAction(String sender) {
        super(sender, null);
    }
}
