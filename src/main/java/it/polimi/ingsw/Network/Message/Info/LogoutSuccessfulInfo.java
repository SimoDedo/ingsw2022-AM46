package it.polimi.ingsw.Network.Message.Info;

/**
 * Info that is sent whenever a user has successfully logged out. It is sent only to the specific user that logged out.
 */
public class LogoutSuccessfulInfo extends  Info {
    public LogoutSuccessfulInfo() {
        super("Successfully logged out");
    }
}
