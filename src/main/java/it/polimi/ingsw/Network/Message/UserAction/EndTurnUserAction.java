package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to end their turn.
 */
public class EndTurnUserAction extends UserAction{

    public EndTurnUserAction(String sender){
        super(sender, UserActionType.END_TURN);
    }
}
