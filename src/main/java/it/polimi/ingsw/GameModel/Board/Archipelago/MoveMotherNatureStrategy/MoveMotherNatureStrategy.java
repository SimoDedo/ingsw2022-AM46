package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.io.InvalidObjectException;
import java.util.List;

/**
 * Interface for MoveMotherNature Strategy pattern. All MoveMotherNature strategies should
 * implement this common interface.
 */
public interface MoveMotherNatureStrategy {
    /**
     * Method that models the movement of the Mother Nature pawn around the archipelago.
     * @param islandTileStarting IslandTile where MotherNature is now
     * @param islandTileDestination IslandTile where MotherNature should end
     * @param moveCount Number of moves allowed
     * @param islandGroups List containing all the IslandGroups to check legal movement
     * @throws InvalidObjectException when the destination island is not within reach
     */
    void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount, List<IslandGroup> islandGroups) throws InvalidObjectException;
}
