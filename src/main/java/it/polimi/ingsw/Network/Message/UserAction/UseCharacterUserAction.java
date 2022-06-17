package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to activate a character.
 */
public class UseCharacterUserAction extends UserAction{

    /**
     * The character chosen to be activated.
     */
    private final int characterID;

    public UseCharacterUserAction(String sender, int characterID) throws  IllegalArgumentException{
        super(sender, UserActionType.USE_CHARACTER);
        this.characterID = characterID;
    }

    public int getCharacterID() {
        return characterID;
    }
}
