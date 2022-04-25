package it.polimi.ingsw.Network.Message.Request;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class CharacterRequest extends Request{

    int usesLeft;

    public CharacterRequest(String recipient,String request, UserActionType expectedUserAction, int usesLeft) {
        super(recipient,request, expectedUserAction);
        this.usesLeft = usesLeft;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

}
