package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.List;

/**
 * This subclass of AbstractCharacter sets the NoEntryTilesSpace on the islands that receive a
 * NoEntryTile. It stores an initial number of NoEntryTiles and has adder/remover methods.
 */
public class NoEntryCharacter extends AbstractCharacter {

    private List<RequestParameter> requestParameters;

    private int noEntryTiles;
    private final int maxNoEntryTiles;

    public NoEntryCharacter(int ID, int cost, int maxNoEntryTiles, List<RequestParameter> requestParameters) {
        super(ID, cost, requestParameters);
        this.noEntryTiles = maxNoEntryTiles;
        this.maxNoEntryTiles = maxNoEntryTiles;
    }

    /**
     * Getter for the number of NoEntryTiles.
     * @return the number of NoEntryTiles on this character
     */
    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    /**
     * Adds one NoEntryTile to this character (unless it already has the maximum number of tiles).
     */
    public void addNoEntryTile() {
        if (noEntryTiles == maxNoEntryTiles) throw new IllegalStateException("Character already has the maximum number of no entry tiles");
        noEntryTiles++;
    }

    /**
     * Removes one NoEntryTile from this character (unless it doesn't have any).
     */
    public void removeNoEntryTile() {
        if (noEntryTiles == 0) throw new IllegalStateException("Character doesn't have any no entry tiles");
        noEntryTiles--;
    }

}
