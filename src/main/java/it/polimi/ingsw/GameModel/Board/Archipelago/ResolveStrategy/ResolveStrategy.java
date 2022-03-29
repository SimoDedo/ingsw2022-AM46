package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Team;

import java.util.List;

public interface ResolveStrategy {
    public Team resolveIslandGroup(IslandGroup islandGroupToResolve, List<Team> teams);
}
