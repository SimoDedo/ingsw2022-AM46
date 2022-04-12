package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Characters.NoEntryCharacter;

public class NoEntryTilesSpace {

    private NoEntryCharacter noEntryCharacter;

    private int noEntryTiles;

    public NoEntryTilesSpace(NoEntryCharacter noEntryCharacter) {
        this.noEntryCharacter = noEntryCharacter;
        noEntryTiles = 0;
    }

    public boolean hasNoEntryTiles() {
        return getNoEntryTiles() > 0;
    }

    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    public void addNoEntryTile() {
        noEntryTiles++;
    }

    public void removeNoEntryTile() {
        if (noEntryTiles >= 0) {
            noEntryTiles--;
            noEntryCharacter.addNoEntryTile();
        } else throw new IllegalStateException("There are zero no-entry tiles placed on this group");
    }
}
