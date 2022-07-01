package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.util.List;

/**
 * Interface to define the method to implement a mother nature strategy
 */
public interface MoveMotherNatureStrategy {
    void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount, List<IslandGroup> islandGroups) throws IllegalArgumentException;
}
