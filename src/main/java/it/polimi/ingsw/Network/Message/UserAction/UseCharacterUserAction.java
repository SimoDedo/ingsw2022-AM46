package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class UseCharacterUserAction extends UserAction{

    private int characterID;

    public UseCharacterUserAction(String sender, int characterID) throws  IllegalArgumentException{
        super(sender, UserActionType.USE_CHARACTER);
        if(characterID< 1 || characterID > 12)
            throw  new IllegalArgumentException("No character with ID "+characterID);
        this.characterID = characterID;
    }

    public int getCharacterID() {
        return characterID;
    }
}
