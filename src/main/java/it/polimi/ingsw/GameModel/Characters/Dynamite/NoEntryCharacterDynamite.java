package it.polimi.ingsw.GameModel.Characters.Dynamite;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;
import java.util.function.Consumer;

public class NoEntryCharacterDynamite implements CharacterDynamite {

    private int ID, cost;

    private boolean isFirstUse, wasUsedThisTurn;

    private Player owner;

    private List<RequestParameters> requestParameters;

    private int noEntryTiles;

    public NoEntryCharacterDynamite(int ID, int cost, int maxNoEntryTiles, List<RequestParameters> requestParameters) {
        this.ID = ID;
        this.noEntryTiles = maxNoEntryTiles;
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
    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) throws IllegalStateException {
        consumer.accept(parameterList);
    }

    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    public void addNoEntryTile() {
        this.noEntryTiles++;
    }

    public void removeNoEntryTile() {
        this.noEntryTiles--;
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
