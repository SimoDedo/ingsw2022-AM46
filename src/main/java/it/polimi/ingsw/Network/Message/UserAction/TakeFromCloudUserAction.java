package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to take the students from a cloud.
 */
public class TakeFromCloudUserAction extends UserAction{

    /**
     * The cloud chosen.
     */
    private final int cloudID;

    public TakeFromCloudUserAction(String sender, int cloudID) {
        super(sender, UserActionType.TAKE_FROM_CLOUD);
        this.cloudID = cloudID;
    }

    public int getCloudID() {
        return cloudID;
    }
}
