package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to log out.
 */
public class LogoutUserAction extends UserAction{
    public LogoutUserAction(String sender) {
        super(sender, UserActionType.DISCONNECT);
    }
}
