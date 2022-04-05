package it.polimi.ingsw.Utils;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests PlayerList specific methods
 */
class PlayerListTest {

    /**
     * Test that tower holder is correctly returned
     */
    @Test
    void getTowerHolder() {
        PlayerList players = new PlayerList();
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        players.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        players.add(new Player("PIETRO", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        assertTrue(players.getTowerHolder(TowerColor.BLACK).getNickname().equals("Simo"));
        assertTrue(players.getTowerHolder(TowerColor.WHITE) == null);
    }

    /**
     * Tests that method correctly counts members in a team
     */
    @Test
    void teamSize() {
        PlayerList players = new PlayerList();
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        players.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        players.add(new Player("PIETRO", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        assertTrue(players.teamSize(TowerColor.BLACK) == 3 && players.teamSize(TowerColor.WHITE) == 0);
    }

    /**
     * Tests that method correctly return new PLayerList containing all and only team members of given color
     */
    @Test
    void getTeam() {
        PlayerList players = new PlayerList();
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        Player player1 = new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig());
        Player player2 = new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig());
        Player player3 = new Player("PIETRO", TowerColor.WHITE, false, gameConfig.getPlayerConfig());
        players.add(player1);
        players.add(player2);
        players.add(player3);
        assertTrue(players.getTeam(TowerColor.BLACK).contains(player1) &&
                players.getTeam(TowerColor.BLACK).contains(player2) &&
                !players.getTeam(TowerColor.BLACK).contains(player3));
        assertTrue(players.getTeam(TowerColor.BLACK).getTowerHolder(TowerColor.BLACK).equals(player1));
    }
}