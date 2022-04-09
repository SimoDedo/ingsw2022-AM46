package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCharacter implements Character {

    private int ID, cost;

    private boolean isFirstUse, wasUsedThisTurn;

    private List<RequestParameters> requestParameters;

    private Player owner;

    public AbstractCharacter(int ID, int cost, List<RequestParameters> requestParameters) {
        this.ID = ID;
        this.cost = cost;
        this.isFirstUse = true;
        this.wasUsedThisTurn = false;
        this.requestParameters = requestParameters;
    }

    public List<RequestParameters> useCharacter(Player owner) {
        if (wasUsedThisTurn) throw new IllegalStateException("Already activated");
        else {
            this.owner = owner;
            if (isFirstUse) {
                isFirstUse = false;
                cost++;
            }
            wasUsedThisTurn = true;
        }
        return requestParameters;
    }

    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) throws IllegalStateException {
        consumer.accept(parameterList);
    }

    public int getCharacterID() {
        return ID;
    }

    public boolean isFirstUse() {
        return isFirstUse;
    }

    public boolean wasUsedThisTurn() {
        return wasUsedThisTurn;
    }

    public int getCost() {
        return cost;
    }

    public void resetUseState() {
        wasUsedThisTurn = false;
        owner = null;
    }

    public Player getOwner() {
        return owner;
    }

}
