package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.Player;

import java.util.List;

/**
 * Model a group of island tiles
 */
public class IslandGroup { //TODO: strategy (if we choose that route)
    /**
     * IslandTiles that compose the IslandGroup
     */
    private List<IslandTile> islandTiles;

    public IslandGroup(boolean isStarting){
        islandTiles.add(new IslandTile(null, -1, isStarting, this)); //TODO: player null replace with "neutral"?
    }

}
