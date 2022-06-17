package it.polimi.ingsw.Network.Message.Info;

/**
 * Info that is sent whenever a user has successfully logged in. It is sent only to the specific user that logged in.
 */
public class LogoutSuccessfulInfo extends  Info {
    public LogoutSuccessfulInfo() {
        super("Successfully logged out");
    }
}
