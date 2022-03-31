package it.polimi.ingsw.GameModel.Board.Player;

public class AssistantCard {
    final private int turnOrder, movePower, ID;

    public AssistantCard(int turnOrder, int movePower) {
        this.turnOrder = turnOrder;
        this.movePower = movePower;
        this.ID = turnOrder;
    }

    public int getID() {
        return ID;
    }

    public int getMovePower() {
        return movePower;
    }

    public int getTurnOrder() {
        return turnOrder;
    }
}
