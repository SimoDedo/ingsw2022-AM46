package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the creations of teams
 */
class TeamManagerTest {

    /**
     * Tests that a 4 player game gives correctly the towers
     */
    @Test
    void createTowerHolder() {
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);
        assertEquals("Simo", players.getTowerHolder(TowerColor.BLACK).getNickname());
        assertEquals("Pirovano", players.getTowerHolder(TowerColor.WHITE).getNickname());
    }

    /**
     * Tests that exception is thrown if inconsistent teams in a 3 player game
     */
    @Test
    void createException3Players() {
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(3);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.GREY);
        teamConfiguration.put("Pirovano", TowerColor.BLACK);
        teamConfiguration.put("Ceruti", TowerColor.GREY);
        TeamManager teamManager = new TeamManager();

        assertThrows(IllegalArgumentException.class, () -> teamManager.create(gameConfig, teamConfiguration));

    }
    /**
     * Tests that exception is thrown if inconsistent teams in a 4 player game
     */
    @Test
    void createException4Players() {
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.BLACK);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();

        assertThrows(IllegalArgumentException.class, () -> teamManager.create(gameConfig, teamConfiguration));

    }
}