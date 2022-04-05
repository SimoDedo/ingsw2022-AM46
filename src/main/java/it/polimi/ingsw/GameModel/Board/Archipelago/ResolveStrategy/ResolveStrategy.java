package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.List;

/**
 * Interface for the strategy used to resolve an island
 */
public interface ResolveStrategy {

    /**
     * Method used to resolve an island
     * @param islandGroupToResolve The island to resolve
     * @param players The players of the current game
     * @param professorSet Manager for the professor, used to know who owns them
     * @return The player holding the towers of the team which holds the most influence, or null if a tie happens
     */
    public Player resolveIslandGroup(IslandGroup islandGroupToResolve, PlayerList players, ProfessorSet professorSet);
}
