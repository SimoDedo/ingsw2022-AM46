package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to move mother nature to a new island.
 */
public class MoveMotherNatureUserAction extends  UserAction{

    /**
     * The island chosen.
     */
    private final int islandID;

    public MoveMotherNatureUserAction(String sender, int islandID) {
        super(sender, UserActionType.MOVE_MOTHER_NATURE);
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
