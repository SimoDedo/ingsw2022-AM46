package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Characters.NoEntryCharacter;

/**
 * Class that models presence of NoEntryTiles on an IslandGroup.
 */
public class NoEntryTilesSpace {

    /**
     * The noEntryTile character who created the space.
     */
    private NoEntryCharacter noEntryCharacter;

    /**
     * The number of noEntryTiles placed on the space.
     */
    private int noEntryTiles;

    public NoEntryTilesSpace(NoEntryCharacter noEntryCharacter) {
        this.noEntryCharacter = noEntryCharacter;
        noEntryTiles = 0;
    }

    /**
     * Checks whether a noEntryTile is placed.
     * @return true if it has at least a tile.
     */
    public boolean hasNoEntryTiles() {
        return getNoEntryTiles() > 0;
    }

    /**
     * Returns the number of noEntryTiles placed on the space.
     * @return the number of noEntryTiles placed on the space.
     */
    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    /**
     * Adds a noEntryTile on the space.
     */
    public void addNoEntryTile() {
        noEntryTiles++;
    }

    /**
     * Removes a noEntryTile from the space.
     */
    public void removeNoEntryTile() {
        if (noEntryTiles >= 0) {
            noEntryTiles--;
            noEntryCharacter.addNoEntryTile();
        } else throw new IllegalStateException("There are zero no-entry tiles placed on this group");
    }
}
