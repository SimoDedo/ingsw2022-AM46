package it.polimi.ingsw.Network.Message.Info;

public class TakeFromCloudInfo extends Info{

    private int cloudIDChosen;

    public TakeFromCloudInfo(int cloudIDChosen) {
        super("Successfully taken students from cloud!");
        this.cloudIDChosen = cloudIDChosen;
    }

    public int getCloudIDChosen() {
        return cloudIDChosen;
    }
}
