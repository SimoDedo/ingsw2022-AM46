package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class LogoutUserAction extends UserAction{
    public LogoutUserAction(String sender) {
        super(sender, UserActionType.DISCONNECT);
    }
}
