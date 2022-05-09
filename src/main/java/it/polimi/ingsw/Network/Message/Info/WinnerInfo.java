package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.TowerColor;

public class WinnerInfo extends Info{

    private TowerColor winner;

    public WinnerInfo(TowerColor winner) {
        super("The winning team is: ");
        this.winner = winner;
    }

    public TowerColor getWinner() {
        return winner;
    }
}
