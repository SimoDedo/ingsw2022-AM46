package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;

import java.util.List;

/**
 * Interface for the strategy used to resolve an island
 */
public interface ResolveStrategy {
    /**
     * Method used to resolve an island
     * @param islandGroupToResolve The island to resolve
     * @param teams The teams of the curretn game
     * @param professorSet Manager for the professor, used to know who owns them
     * @return The team which holds the most influence, or null if a tie happens
     */
    public Team resolveIslandGroup(IslandGroup islandGroupToResolve, List<Team> teams, ProfessorSet professorSet);
}
