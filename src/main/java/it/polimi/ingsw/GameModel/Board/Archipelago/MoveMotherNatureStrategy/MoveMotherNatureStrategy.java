package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.io.InvalidObjectException;
import java.util.List;

public interface MoveMotherNatureStrategy {
    public void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount ,List<IslandGroup> islandGroups) throws InvalidObjectException;
}
