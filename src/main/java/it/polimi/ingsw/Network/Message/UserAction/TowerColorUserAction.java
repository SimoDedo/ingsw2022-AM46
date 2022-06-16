package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to select a tower color.
 */
public class TowerColorUserAction extends UserAction{
    private final TowerColor towerColor;

    public TowerColorUserAction(String sender, TowerColor towerColor) {
        super(sender, UserActionType.TOWER_COLOR);
        this.towerColor = towerColor;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}
