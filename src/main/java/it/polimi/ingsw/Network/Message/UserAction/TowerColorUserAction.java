package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public class TowerColorUserAction extends UserAction{
    private TowerColor towerColor;

    public TowerColorUserAction(String sender, TowerColor towerColor) {
        super(sender, UserActionType.TOWER_COLOR);
        this.towerColor = towerColor;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}
