package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.util.List;

/**
 * Strategy to move MotherNature when no character is activated
 */
public class StandardMotherNatureStrategy implements MotherNatureStrategy{
    /**
     * Moves motherNature to the destination, throws exception if movement is not allowed
     * @param islandTileStarting IslandTile where MotherNature is now
     * @param islandTileDestination IslandTile where MotherNature should end
     * @param moveCount Number of moves allowed
     * @param islandGroups List containing all the IslandGroups to check legal movement
     */
    @Override
    public void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount, List<IslandGroup> islandGroups) {

    }
}
