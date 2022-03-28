package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import com.sun.tools.javac.main.Option;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;

import java.util.List;

public interface MotherNatureStrategy {
    public void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount ,List<IslandGroup> islandGroups) throws Option.InvalidValueException;
}
