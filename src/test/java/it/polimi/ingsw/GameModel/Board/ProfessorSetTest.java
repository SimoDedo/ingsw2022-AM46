package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.Player.TeamManager;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests ProfessorSet class
 */
class ProfessorSetTest {

    /**
     * Test that method correctly sets the owner
     */
    @Test
    void setOwner() {
        Bag bag = new Bag();
        bag.fillRemaining();
        PlayerConfig playerConfig = new PlayerConfig(4);
        playerConfig.setBag(bag);
        ProfessorSet professorSet = new ProfessorSet();
        Player player1 = new Player("tizio", TowerColor.BLACK, true, playerConfig);
        Player player2 = new Player("caio", TowerColor.BLACK, true, playerConfig);
        professorSet.setOwner(Color.RED, player1);
        professorSet.setOwner(Color.PINK, player2);
        assertTrue(professorSet.getProfessor(Color.RED).getOwner().equals(player1));
        assertTrue(professorSet.getProfessor(Color.PINK).getOwner().equals(player2));
        assertTrue(professorSet.getProfessor(Color.GREEN).getOwner() == null);
    }

    /**
     * Test that method count correctly number of professor each team has
     * @throws FullTeamException When team is already full (not tested here)
     */
    @Test
    void getNumberOfProfessors() throws FullTeamException {
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.BLUE, players.getTeam(TowerColor.BLACK).get(1));
        professorSet.setOwner(Color.GREEN, players.getTeam(TowerColor.WHITE).get(1));
        professorSet.setOwner(Color.YELLOW, players.getTeam(TowerColor.WHITE).get(0));
        assertTrue(professorSet.getNumberOfProfessors(TowerColor.BLACK) == 3 && professorSet.getNumberOfProfessors(TowerColor.WHITE) == 2);
    }
}