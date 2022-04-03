package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;

/**
 * Models character number 2
 */
public class CheckAndMoveProfessorCharacter implements Character{ //TODO: finish implementing when C&M strategy is done
    private int ID = 0;

    private Player playerUsing = null;

    private int cost = 0;

    private boolean usedThisTurn;

    private boolean isFirstUse;

    private int maxUses = 1;

    private int uses = 0;

    private List<RequestParameters> requestParameters;



    public CheckAndMoveProfessorCharacter(int ID, int cost, List<RequestParameters> requestParameters, MoveMotherNatureStrategy moveMotherNatureStrategy) {
        isFirstUse = true;
        this.ID = ID;
        this.cost = cost;
        this.maxUses = 1;
        this.requestParameters = requestParameters;

    }

    @Override
    public List<RequestParameters> useCharacter(int ID, Player player) throws IllegalArgumentException, IllegalStateException {
        if (this.ID != ID)
            throw new IllegalArgumentException("Trying to call character " + ID + " instead of" + this.ID);
        else if (usedThisTurn)
            throw new IllegalStateException("Already activated");
        else {
            this.playerUsing = player;
            if (!isFirstUse)
                isFirstUse = false;
            usedThisTurn = true;
        }
        return requestParameters;
    }

    @Override
    public int getCharacterID() {
        return ID;
    }

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

    @Override
    public int getCost() {
        return isFirstUse ? cost : cost + 1;
    }

    @Override
    public void resetUseState() {
        usedThisTurn = false;
        uses = 0;
    }

    public MoveMotherNatureStrategy useAbilityC4() throws IllegalAccessException {
        return null;
    }
}
