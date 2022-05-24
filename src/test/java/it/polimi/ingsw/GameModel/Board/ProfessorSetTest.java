package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

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
        assertEquals(professorSet.getProfessor(Color.RED).getOwner(), player1, "professor has not been assigned properly to the player");
        assertEquals(professorSet.getProfessor(Color.PINK).getOwner(), player2, "professor has not been assigned properly to the player");
        assertNull(professorSet.getProfessor(Color.GREEN).getOwner(), "unassigned professor has an owner");
    }

    /**
     * Test that the getNumberOfProfessors method correctly counts the number of professor each team has.
     */
    @Test
    void getNumberOfProfessors() {
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        PlayerList players = new PlayerList();
        players.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        players.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.BLUE, players.getTeam(TowerColor.BLACK).get(1));
        professorSet.setOwner(Color.GREEN, players.getTeam(TowerColor.WHITE).get(1));
        professorSet.setOwner(Color.YELLOW, players.getTeam(TowerColor.WHITE).get(0));
        assertEquals(professorSet.getNumberOfProfessors(TowerColor.BLACK),3, "unexpected number of professors owned by black team");
        assertEquals(professorSet.getNumberOfProfessors(TowerColor.WHITE),2, "unexpected number of professors owned by white team");
    }
}