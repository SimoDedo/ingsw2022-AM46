package it.polimi.ingsw.GameModel.Board.Archipelago;

import java.io.Serializable;

/**
 * Class that represents MotherNature pawn in game
 */
public class MotherNature implements Serializable {

    /**
     * contains the IslandTile which currently holds MotherNature
     */
    IslandTile islandTile;

    /**
     * Instantiates MotherNature with given IslandTile
     * @param islandTile the island tile that initially contains mother nature
     */
    public MotherNature(IslandTile islandTile) {
        this.islandTile = islandTile;
    }

    /**
     * Getter for the IslandTile
     * @return the island tile currently containing mother nature
     */
    public IslandTile getIslandTile() {
        return islandTile;
    }

    /**
     * Setter for the IslandTile
     * @param islandTile the island tile that will contain mother nature
     */
    public void setIslandTile(IslandTile islandTile) {
        this.islandTile = islandTile;
    }

}
