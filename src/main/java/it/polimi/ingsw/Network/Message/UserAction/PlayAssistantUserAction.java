package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class PlayAssistantUserAction  extends UserAction{

    private final int assistantID;

    public PlayAssistantUserAction(String sender, int assistantID) throws IllegalArgumentException{
        super(sender, UserActionType.PLAY_ASSISTANT);
        if(assistantID<1 || assistantID>10)
            throw  new IllegalArgumentException("No assistant with  "+ assistantID +"exists");
        this.assistantID = assistantID;
    }

    public int getAssistantID() {
        return assistantID;
    }
}
