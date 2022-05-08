package it.polimi.ingsw.GameModel.Board.Player;

import java.io.Serializable;

public class AssistantCard implements Serializable {
    final private int turnOrder, movePower, ID;

    public AssistantCard(int turnOrder, int movePower) {
        this.turnOrder = turnOrder;
        this.movePower = movePower;
        this.ID = turnOrder;
    }

    /**
     * @return the unique ID for this assistant, which also corresponds to how much it can move mother nature
     */
    public int getID() {
        return ID;
    }

    public int getMovePower() {
        return movePower;
    }

    /**
     * @return the value which determines turn order (lowest goes first)
     */
    public int getTurnOrder() {
        return turnOrder;
    }
}
