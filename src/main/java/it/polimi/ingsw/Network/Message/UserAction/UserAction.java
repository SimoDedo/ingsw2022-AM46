package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Utils.Enum.UserActionType;


public abstract class UserAction extends Message {

    private final UserActionType userActionType;

    public UserAction(String sender, UserActionType userActionType) {
        super(sender);
        this.userActionType = userActionType;
    }

    public UserActionType getUserActionType() {
        return userActionType;
    }
}
