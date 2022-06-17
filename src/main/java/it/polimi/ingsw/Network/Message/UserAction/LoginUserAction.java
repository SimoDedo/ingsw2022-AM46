package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to log in with a nickname.
 */
public class LoginUserAction extends UserAction{

    /**
     * The nickname chosen by the user
     */
    String nickname;

    public LoginUserAction(String sender) throws IllegalArgumentException{
        super(sender, UserActionType.LOGIN);
        this.nickname = sender;
    }

    public String getNickname() {
        return nickname;
    }

}
