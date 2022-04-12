package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for the common methods shared by all the different types of Character.
 */
public interface Character {

    /**
     * Precedes a character's ability activation. The character sets its activator as its owner,
     * and returns a list of parameters it needs in order for its ability to activate. This list
     * will be picked up by the controller.
     * @param owner the player who activated this character
     * @return a list of RequestParameters that will be needed by the game controller
     */
    List<RequestParameter> useCharacter(Player owner);

    /**
     * Method that executes the character's ability. It passes the list of parameters (in the form
     * of IDs of various pawns/board pieces) to the right Consumer according to the character's ID.
     * @param consumer the Consumer that acts on the GameModel
     * @param parameterList the list of the consumer's parameters
     */
    void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList);

    /**
     * Returns the ID of the Character
     * @return the ID of the Character
     */
    int getCharacterID();

    /**
     * True if it's the first time the Character is being used
     * @return isFirstUse
     */
    boolean isFirstUse();

    /**
     * True if Character was used (paid for) this turn
     * @return usedThisTurn
     */
    boolean wasUsedThisTurn();

    /**
     * Getter for cost of the Character
     * @return the current cost (standard, or incremented by one if the character has already been used once)
     */
    int getCost();

    /**
     * Resets the state of the character (uses, owner etc.). Will be called once the turn ends
     */
    void resetUseState();

    /**
     * Getter for the character's owner.
     * @return the current owner (activator) of this character
     */
    Player getOwner();

}