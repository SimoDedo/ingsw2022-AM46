package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * This abstract class implements the common methods shared by all the different types of Character.
 */
public abstract class AbstractCharacter implements Character, Serializable {

    private int ID, cost;

    private boolean isFirstUse, wasUsedThisTurn, abilityUsed;

    private final List<RequestParameter> requestParameters;

    private Player owner;

    public AbstractCharacter(int ID, int cost, List<RequestParameter> requestParameters) {
        this.ID = ID;
        this.cost = cost;
        this.isFirstUse = true;
        this.wasUsedThisTurn = false;
        this.abilityUsed = false;
        this.requestParameters = requestParameters;
    }

    /**
     * Precedes a character's ability activation. The character sets its activator as its owner.
     * If this is the first time the character is being used in the game, the cost is increased by
     * one. If the character requires a dining room student, it checks if at least one is present in the player's
     * dining room, otherwise throws an exception.
     * Finally, the method returns a list of parameters it needs in order for its ability to
     * activate, which will be picked up by the controller.
     * @param owner the player who activated this character
     * @return a list of RequestParameters that will be needed by the game controller
     */
    public List<RequestParameter> useCharacter(Player owner) throws  IllegalStateException{
        if (wasUsedThisTurn) throw new IllegalStateException("Already activated.");
        if(requestParameters.contains(RequestParameter.STUDENT_DINING_ROOM)){
            int emptyTables = 0;
            for(Color color : Color.values()){
                if(owner.getTableStudentsIDs(color).size() == 0)
                    emptyTables++;
            }
            if(emptyTables == 5)
                throw new IllegalStateException("This character requires at least one student to be in your Dining Room.");
        }

        this.owner = owner;
        if (isFirstUse) {
            isFirstUse = false;
            cost++;
        }
        wasUsedThisTurn = true;

        return requestParameters;
    }

    /**
     * Method that executes the character's ability. It passes the list of parameters (in the form
     * of IDs of various pawns/board pieces) to the right Consumer according to the character's ID.
     * @param consumer the Consumer that acts on the GameModel
     * @param parameterList the list of the consumer's parameters
     */
    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) throws IllegalStateException, LastRoundException, GameOverException {
        if(!abilityUsed){
            consumer.accept(parameterList);
            abilityUsed = true;
        }
        else throw  new IllegalStateException("Character ability already used this turn");
    }

    /**
     * Returns the ID of the Character
     * @return the ID of the Character
     */
    public int getCharacterID() {
        return ID;
    }

    /**
     * True if it's the first time the Character is being used
     * @return isFirstUse
     */
    public boolean isFirstUse() {
        return isFirstUse;
    }

    /**
     * True if Character was used (paid for) this turn
     * @return usedThisTurn
     */
    public boolean wasUsedThisTurn() {
        return wasUsedThisTurn;
    }

    /**
     * Getter for cost of the Character
     * @return the current cost (standard, or incremented by one if the character has already been used once)
     */
    public int getCost() {
        return cost;
    }

    /**
     * Resets the state of the character (uses, owner etc.). Will be called once the turn ends
     */
    public void resetUseState() {
        wasUsedThisTurn = false;
        abilityUsed = false;
        owner = null;
    }

    /**
     * Getter for the character's owner.
     * @return the current owner (activator) of this character
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Return the maximum number of times the ability can be used.
     * @return always 1. If specific character have different max uses the method will be overridden.
     */
    public int getMaxUses(){
        return 1;
    }

    /**
     * Return the maximum number of times the ability can be used.
     * @return always 1. If specific character have different max uses the method will be overridden.
     */
    public int getUsesLeft(){
        return abilityUsed ? 0 : 1;
    }

}
