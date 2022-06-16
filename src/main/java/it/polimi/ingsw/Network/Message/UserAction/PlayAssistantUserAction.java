package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to play an assistant.
 */
public class PlayAssistantUserAction  extends UserAction{

    /**
     * The assistant chosen
     */
    private final int assistantID;

    public PlayAssistantUserAction(String sender, int assistantID) throws IllegalArgumentException{
        super(sender, UserActionType.PLAY_ASSISTANT);
        this.assistantID = assistantID;
    }

    public int getAssistantID() {
        return assistantID;
    }
}
