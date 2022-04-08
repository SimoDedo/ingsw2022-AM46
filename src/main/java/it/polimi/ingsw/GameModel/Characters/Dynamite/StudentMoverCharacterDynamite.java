package it.polimi.ingsw.GameModel.Characters.Dynamite;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;
import java.util.function.Consumer;

public class StudentMoverCharacterDynamite extends StudentContainer implements CharacterDynamite {

    private int ID, cost, usesLeft, maxUses;

    private boolean isFirstUse, wasUsedThisTurn;

    private Player owner;

    List<RequestParameters> requestParameters;

    public StudentMoverCharacterDynamite(int ID, int cost, int maxUses, int maxPawns, List<RequestParameters> requestParameters) {
        super(null, maxPawns);
        this.ID = ID;
        this.maxUses = maxUses;
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
        usesLeft = maxUses;
        owner = null;
    }
}
