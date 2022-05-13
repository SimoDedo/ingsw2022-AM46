package it.polimi.ingsw.Network.Message.Info;

public class NextTurnInfo extends Info{

    private final String nextPlayer;

    public NextTurnInfo(String nextPlayer) {
        super("Next player to play is: ");
        this.nextPlayer = nextPlayer;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public String toString() {
        return super.toString() + nextPlayer;
    }
}
