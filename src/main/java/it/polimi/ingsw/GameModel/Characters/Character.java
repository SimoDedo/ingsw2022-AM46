package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;

/**
 * Interface common to all 12 Characters
 */
public interface Character {
    /**
     * Method called when a Player tries to pay and use a Character
     * @param ID ID of the Character
     * @param player Player who is spending coins
     * @return A list of parameters which are needed to consequently use its ability
     * @throws IllegalArgumentException When wrong ID is given
     * @throws IllegalAccessException When it was already activated this turn
     */
    public List<RequestParameters> useCharacter(int ID, Player player) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the ID of the Character
     * @return the ID of the Character
     */
    public int getCharacterID();

    /**
     * True if it's the first time the Character is being used
     * @return isFirstUse
     */
    public boolean isFirstUse();

    /**
     * True if Character was used (paid) this turn
     * @return usedThisTurn
     */
    public boolean wasUsedThisTurn();

    /**
     * The cost of the Character
     * @return The cost already incremented if this isn't its first use
     */
    public int getCost();

    /**
     * Resets uses and sets usedThisTurn to false. Will be called once the turn ends
     */
    public void resetUseState();


}
