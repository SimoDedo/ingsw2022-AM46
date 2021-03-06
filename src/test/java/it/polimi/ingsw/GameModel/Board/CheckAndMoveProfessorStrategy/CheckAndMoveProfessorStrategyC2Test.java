package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test strategy for assigning professor ownership when C2 is activated
 */
class CheckAndMoveProfessorStrategyC2Test {

    /**
     * Tests that when strategy is called, it correctly assigns professor.
     * In this test only one player has one red student placed, thus will hold the professor.
     * Afterwards, another red student gets placed in another player's DN and the professor should change
     * ownership, since C2 is active
     */
    @Test
    void checkAndMoveProfessor() throws FullTableException {
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
        CheckAndMoveProfessorStrategyC2 checkAndMoveProfessorStrategyC2 = new CheckAndMoveProfessorStrategyC2(players.getByNickname("Greg"));
        professorSet.setCheckAndMoveProfessorStrategy(checkAndMoveProfessorStrategyC2);

        players.getByNickname("Simo").addToDR(new Student(Color.RED, null));
        professorSet.checkAndMoveProfessor(players, Color.RED);
        assertEquals("Simo", professorSet.getProfessorsOwner().get(Color.RED),
                "Player who holds the most student of a color should be the owner");

        players.getByNickname("Greg").addToDR(new Student(Color.RED, null));
        professorSet.checkAndMoveProfessor(players, Color.RED);
        assertEquals("Greg", professorSet.getProfessorsOwner().get(Color.RED),
                "Since C2 is active, ownership should be changed even if number of students was only tied");
    }
}