package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.List;

public class NoEntryCharacter extends AbstractCharacter {

    private List<RequestParameters> requestParameters;

    private int noEntryTiles;
    private int maxNoEntryTiles;

    public NoEntryCharacter(int ID, int cost, int maxNoEntryTiles, List<RequestParameters> requestParameters) {
        super(ID, cost, requestParameters);
        this.noEntryTiles = maxNoEntryTiles;
        this.maxNoEntryTiles = maxNoEntryTiles;
    }

    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    public void addNoEntryTile() {
        if (noEntryTiles == maxNoEntryTiles) throw new IllegalStateException("Character already has the maximum number of no entry tiles");
        noEntryTiles++;
    }

    public void removeNoEntryTile() {
        if (noEntryTiles == 0) throw new IllegalStateException("Character doesn't have any no entry tiles");
        noEntryTiles--;
    }

}
