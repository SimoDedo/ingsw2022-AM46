package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class MoveMotherNatureUserAction extends  UserAction{
    private int islandID;

    public MoveMotherNatureUserAction(String sender, int islandID) {
        super(sender, UserActionType.MOVE_MOTHER_NATURE);
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
