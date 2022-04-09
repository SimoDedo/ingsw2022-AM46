package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;
import java.util.function.Consumer;

public interface Character {

    List<RequestParameters> useCharacter(Player owner);

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
     * True if Character was used (paid) this turn
     * @return usedThisTurn
     */
    boolean wasUsedThisTurn();

    /**
     * The cost of the Character
     * @return The cost already incremented if this isn't its first use
     */
    int getCost();

    /**
     * Resets uses and sets usedThisTurn to false. Will be called once the turn ends
     */
    void resetUseState();

    Player getOwner();

}