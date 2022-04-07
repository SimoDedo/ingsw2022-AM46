package it.polimi.ingsw.GameModel.Board.Player;

/**
 * Class that models the Assistant cards present in each Wizard deck.
 */
public class AssistantCard {

    /**
     * Variable that determines the turn order of this card. The lower the number, the earlier this
     * card will be played.
     */
    final private int turnOrder;

    /**
     * Variable that determines the moving power of this card. The higher the number, the more hops
     * Mother Nature can make with this card.
     */
    final private int movePower;

    final private int ID; // deprecated

    /**
     * Constructor for AssistantCard with the given turn order and move power.
     * @param turnOrder the turn order of this card
     * @param movePower the move power of this card
     */
    public AssistantCard(int turnOrder, int movePower) {
        this.turnOrder = turnOrder;
        this.movePower = movePower;
        this.ID = turnOrder;
    }

    public int getID() { // deprecated
        return ID;
    }

    /**
     * Getter for the move power of this card.
     * @return the movePower of this card
     */
    public int getMovePower() {
        return movePower;
    }

    /**
     * Getter for the turn order of this card.
     * @return the turnOrder of this card
     */
    public int getTurnOrder() {
        return turnOrder;
    }
}
