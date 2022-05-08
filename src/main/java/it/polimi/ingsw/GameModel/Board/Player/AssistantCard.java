package it.polimi.ingsw.GameModel.Board.Player;

public class AssistantCard {
    final private int turnOrder, movePower, ID;

    public AssistantCard(int turnOrder, int movePower) {
        this.turnOrder = turnOrder;
        this.movePower = movePower;
        this.ID = turnOrder;
    }

    /**
     * @return the unique ID for this assistant, which also corresponds to its turn order weight
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
