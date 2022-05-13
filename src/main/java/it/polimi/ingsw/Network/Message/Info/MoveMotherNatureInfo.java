package it.polimi.ingsw.Network.Message.Info;

public class MoveMotherNatureInfo extends Info{

    private final int islandID;

    public MoveMotherNatureInfo(int islandID) {
        super("Mother nature successfully moved!");
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
