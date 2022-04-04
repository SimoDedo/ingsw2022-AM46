package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import org.junit.jupiter.api.Test;

import javax.swing.*;

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
        ProfessorSet professorSet = new ProfessorSet();
        Player player1 = new Player("tizio", 8, TowerColor.BLACK, 2, new Bag().drawN(7));
        Player player2 = new Player("caio", 8, TowerColor.BLACK, 2, new Bag().drawN(7));
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
        Team team1 = new Team(TowerColor.BLACK, 4);
        team1.addMember("tizio",4, new Bag().drawN(7));
        team1.addMember("caio",4, new Bag().drawN(7));
        Team team2 = new Team(TowerColor.WHITE, 4);
        team2.addMember("caio",4, new Bag().drawN(7));
        team2.addMember("caio",4, new Bag().drawN(7));
        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, team1.getMembers().get(0));
        professorSet.setOwner(Color.RED, team1.getMembers().get(1));
        professorSet.setOwner(Color.BLUE, team1.getMembers().get(0));
        professorSet.setOwner(Color.GREEN, team2.getMembers().get(0));
        professorSet.setOwner(Color.YELLOW, team2.getMembers().get(0));
        assertTrue(professorSet.getNumberOfProfessors(team1) == 3 && professorSet.getNumberOfProfessors(team2) == 2);
    }
}