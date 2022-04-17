package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.TeamManager;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test standard strategy for assigning professor ownership
 */
class CheckAndMoveProfessorStrategyStandardTest {

    /**
     * Tests that when strategy is called, it correctly assigns professor.
     * In this test only one player has one red student placed, thus will hold the professor.
     * Afterwards, another red student gets placed in another player's DN but the professor shouldn't change ownership
     */
    @Test
    void checkAndMoveProfessor() throws FullTableException {
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

        ProfessorSet professorSet = new ProfessorSet();

        players.getByNickname("Simo").addToDR(new Student(Color.RED, null));
        professorSet.checkAndMoveProfessor(players, Color.RED);
        assertEquals(professorSet.getProfessorsOwner().get(Color.RED), "Simo",
                "Player who holds the most student of a color should be the owner");

        players.getByNickname("Greg").addToDR(new Student(Color.RED, null));
        assertEquals(professorSet.getProfessorsOwner().get(Color.RED), "Simo",
                "Player who previously held the professors' ownership should maintain it even when someone ties their student count");
    }
}