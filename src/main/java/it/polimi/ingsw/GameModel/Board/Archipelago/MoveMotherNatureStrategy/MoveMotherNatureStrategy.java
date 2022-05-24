package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.util.List;

public interface MoveMotherNatureStrategy {
    void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount, List<IslandGroup> islandGroups) throws IllegalArgumentException;
}
