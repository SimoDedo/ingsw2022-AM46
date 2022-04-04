package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC3;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC8;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC9;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;

/**
 * Models character number 3,6,8,9
 */
public class ResolveStrategyCharacter implements Character {

    /**
     * ID of the Character
     */
    private int ID;

    /**
     * The player who paid for the Character
     */
    private Player playerUsing = null;

    /**
     * The cost of the Character
     */
    private int cost = 0;

    /**
     * True when someone paid for the Character during this turn, false otherwise
     */
    private boolean usedThisTurn;

    /**
     * True if no one has paid for this Character yet
     */
    private boolean isFirstUse;

    /**
     * The maximum number of time the Character Ability can be used in one turn
     */
    private int maxUses = 1;

    /**
     * The number of times the Character Ability has been used this turn
     */
    private int uses = 0;

    /**
     * List of the parameter the Character requests in order to apply its Ability
     */
    private List<RequestParameters> requestParameters;

    /**
     * The strategy given to be used by Resolve method
     */
    private ResolveStrategy resolveStrategy;

    /**
     * Constructor for the Character
     * @param ID ID of the Character
     * @param cost Cost of the Character
     * @param requestParameters Parameters required for the useAbility function
     * @param resolveStrategy Strategy to be used
     */
    public ResolveStrategyCharacter(int ID, int cost, List<RequestParameters> requestParameters, ResolveStrategy resolveStrategy) {
        isFirstUse = true;
        this.ID = ID;
        this.cost = cost;
        this.maxUses = 1;
        this.requestParameters = requestParameters;
        this.resolveStrategy = resolveStrategy;
    }

    /**
     * Method called when a Player tries to pay and use a Character
     * @param ID ID of the Character
     * @param player Player who is spending coins
     * @return A list of parameters which are needed to consequently use its ability
     * @throws IllegalArgumentException When wrong ID is given
     * @throws IllegalAccessException When it was already activated this turn
     */
    @Override
    public List<RequestParameters> useCharacter(int ID, Player player) throws IllegalArgumentException, IllegalStateException {
        if (this.ID != ID)
            throw new IllegalArgumentException("Trying to call character " + ID + " instead of" + this.ID);
        else if (usedThisTurn)
            throw new IllegalStateException("Already activated");
        else {
            this.playerUsing = player;
            if (isFirstUse)
                isFirstUse = false;
            usedThisTurn = true;
        }
        return requestParameters;
    }

    /**
     * Returns the ID of the Character
     * @return the ID of the Character
     */
    @Override
    public int getCharacterID() {
        return ID;
    }

    /**
     * True if it's the first time the Character is being used
     * @return isFirstUse
     */
    @Override
    public boolean isFirstUse() {
        return isFirstUse;
    }

    /**
     * True if Character was used (paid) this turn
     * @return usedThisTurn
     */
    @Override
    public boolean wasUsedThisTurn() {
        return usedThisTurn;
    }

    /**
     * The cost of the Character
     * @return The cost already incremented if this isn't its first use
     */
    @Override
    public int getCost() {
        return isFirstUse ? cost : cost + 1;
    }

    /**
     * Resets uses and sets usedThisTurn to false. Will be called once the turn ends
     */
    @Override
    public void resetUseState() {
        usedThisTurn = false;
        uses = 0;
    }

    /**
     * Uses the ability of Character 3
     * @param islandTile
     * @return The strategy to be used
     * @throws IllegalAccessException When no more uses are available
     */
    public ResolveStrategy useAbilityC3(IslandTile islandTile) throws IllegalAccessException {
        if (uses < maxUses) {
            uses++;
            ((ResolveStrategyC3) resolveStrategy).setIslandTileSelected(islandTile);
            return resolveStrategy;
        } else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 6
     * @return The strategy to be used
     * @throws IllegalAccessException When no more uses are available
     */
    public ResolveStrategy useAbilityC6() throws IllegalAccessException {
        if (uses < maxUses) {
            uses++;
            return resolveStrategy;
        } else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 8
     * @return The strategy to be used
     * @throws IllegalAccessException When no more uses are available
     */
    public ResolveStrategy useAbilityC8() throws IllegalAccessException {
        if (uses < maxUses) {
            uses++;
            ((ResolveStrategyC8) resolveStrategy).setActivatingPlayer(playerUsing);
            return resolveStrategy;
        } else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 9
     * @param color
     * @return The strategy to be used
     * @throws IllegalAccessException When no more uses are available
     */
    public ResolveStrategy useAbilityC9(Color color) throws IllegalAccessException {
        if (uses < maxUses) {
            uses++;
            ((ResolveStrategyC9) resolveStrategy).setColorToIgnore(color);
            return resolveStrategy;
        } else throw new IllegalAccessException("No more uses available");
    }
}
