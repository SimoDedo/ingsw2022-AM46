package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class LoginUserAction extends UserAction{

    String nickname;

    public LoginUserAction(String sender) throws IllegalArgumentException{
        super(sender, UserActionType.LOGIN);
        if(sender == null || sender.equals("")) {
            throw new IllegalArgumentException("Invalid nickname");
        }
        this.nickname = sender;
    }

    public String getNickname() {
        return nickname;
    }

}
