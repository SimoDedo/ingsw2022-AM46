package it.polimi.ingsw.GameModel.Board.Archipelago;

/**
 * Class that represents the MotherNature pawn in a game.
 */
public class MotherNature { //CHECKME: should be a singleton? It could cause issues if we opt for multiple games

    /**
     * Variable that contains the IslandTile which currently holds MotherNature
     */
    IslandTile islandTile;

    /**
     * Constructor that instantiates MotherNature with the given IslandTile as its owner.
     * @param islandTile tile that holds MotherNature
     */
    public MotherNature(IslandTile islandTile) {
        this.islandTile = islandTile;
    }

    /**
     * Getter for the IslandTile that holds MotherNature.
     * @return the holder of MotherNature
     */
    public IslandTile getIslandTile() {
        return islandTile;
    }

    /**
     * Setter for the IslandTile that holds MotherNature.
     * @param islandTile the future holder of MotherNature
     */
    public void setIslandTile(IslandTile islandTile) {
        this.islandTile = islandTile;
    }

}
