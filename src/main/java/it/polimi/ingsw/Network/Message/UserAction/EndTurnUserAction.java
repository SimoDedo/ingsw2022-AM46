package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class EndTurnUserAction extends UserAction{

    public EndTurnUserAction(String sender){
        super(sender, UserActionType.END_TURN);
    }
}
