package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;

import java.util.List;

/**
 * Interface for ResolveIslandGroup Strategy pattern. All resolveIslandGroup strategies should
 * implement this common interface.
 */
public interface ResolveStrategy {

    /**
     * Method that models the resolution of an island group.
     * @param islandGroupToResolve The group to resolve
     * @param teams The teams inside the current game
     * @param professorSet Manager for the professor, used to acknowledge who owns each professor
     * @return The team which holds the most influence, or null if a tie happens
     */
    Team resolveIslandGroup(IslandGroup islandGroupToResolve, List<Team> teams, ProfessorSet professorSet);
}
