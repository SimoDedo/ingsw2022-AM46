package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class TakeFromCloudUserAction extends UserAction{

    private  int cloudID;

    public TakeFromCloudUserAction(String sender, int cloudID) {
        super(sender, UserActionType.TAKE_FROM_CLOUD);
        this.cloudID = cloudID;
    }

    public int getCloudID() {
        return cloudID;
    }
}
