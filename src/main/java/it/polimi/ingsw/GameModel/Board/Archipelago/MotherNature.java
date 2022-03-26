package it.polimi.ingsw.GameModel.Board.Archipelago;

/**
 * Class that represents MotherNature pawn in game
 */
public class MotherNature { //TODO: should be a singleton? could cause issues if we opt for multiple games

    /**
     * contains the IslandTile which currently holds MotherNature
     */
    IslandTile islandTile;

    /**
     * Instantiates MotherNature with given IslandTIle
     * @param islandTile
     */
    public MotherNature(IslandTile islandTile) {
        this.islandTile = islandTile;
    }

    /**
     * Getter for the IslandTIle
     * @return
     */
    public IslandTile getIslandTile() {
        return islandTile;
    }

    /**
     * Setter for the IslandTile
     * @param islandTile
     */
    public void setIslandTile(IslandTile islandTile) {
        this.islandTile = islandTile;
    }


}
