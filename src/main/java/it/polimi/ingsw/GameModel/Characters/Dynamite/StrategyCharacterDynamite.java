package it.polimi.ingsw.GameModel.Characters.Dynamite;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;
import java.util.function.Consumer;

public class StrategyCharacterDynamite implements CharacterDynamite {

    private int ID, cost;

    private boolean isFirstUse, wasUsedThisTurn;

    private Player owner = null;

    private List<RequestParameters> requestParameters;

    public StrategyCharacterDynamite(int ID, int cost, List<RequestParameters> requestParameters) {
        this.ID = ID;
        this.cost = cost;
        this.requestParameters = requestParameters;
    }

    @Override
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

    @Override
    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) {
        consumer.accept(parameterList);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public int getCharacterID() {
        return ID;
    }

    @Override
    public boolean isFirstUse() {
        return isFirstUse;
    }

    @Override
    public boolean wasUsedThisTurn() {
        return wasUsedThisTurn;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public void resetUseState() {
        wasUsedThisTurn = false;
        owner = null;
    }


}
