package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for game expert class to ensure added functionality works correctly.
 * Testing methodology follows that of Game.
 */
class GameExpertTest {

    /**
     * Along with testing method functionality (same as game), it checks that it correctly awards coin since
     * we placed two students in the table beforehand.
     */
    @Test
    void moveStudentFromEntrance() throws FullTableException {
        GameFactory gameFactory = new GameFactory();
        GameExpert game = (GameExpert) gameFactory.create(4, GameMode.EXPERT);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        Map.Entry<Integer, Color> stud = game.getEntranceStudentsIDs("Simo").entrySet().stream().toList().get(0);
        game.players.getByNickname("Simo").addToDR(new Student(stud.getValue(), null));
        game.players.getByNickname("Simo").addToDR(new Student(stud.getValue(), null));
        game.moveStudentFromEntrance("Simo", stud.getKey(), game.getTableIDs("Simo").get(stud.getValue()));

        assertTrue(game.getTableStudentsIDs("Simo", stud.getValue()).contains(stud.getKey()),
                "Table should contain Student moved");
        assertFalse(game.getEntranceStudentsIDs("Simo").containsKey(stud.getKey()),
                "Entrance should no longer contain Student moved");
        assertEquals(1, game.getCoins("Simo"),
                "A coin should be awarded to the player since it moved the third studnt in a table");
    }

    /**
     * Tests that character created are always 3 and different from one another.
     */
    @RepeatedTest(10)
    void createCharacters() {
        GameFactory gameFactory = new GameFactory();
        GameExpert game = (GameExpert) gameFactory.create(4, GameMode.EXPERT);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.createCharacters();

        // System.out.println("Characters drawn: " + game.getDrawnCharacterIDs());
        assertEquals(3, game.getDrawnCharacterIDs().stream().distinct().toList().size(),
                "Always 3 different characters should be drawn");
    }

    /**
     * Tests that each player is given exactly one coin
     */
    @Test
    void distributeInitialCoins() {
        GameFactory gameFactory = new GameFactory();
        GameExpert game = (GameExpert) gameFactory.create(4, GameMode.EXPERT);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.distributeInitialCoins();

        for(Player player : game.players){
            assertEquals(1, game.getCoins(player.getNickname()),
                    "Each player should have one coin");
        }
    }

    /**
     * Tests that characters are correctly used and that multiple character usage isn't permitted.
     */
    @RepeatedTest(5)
    void useCharacter() {
        GameFactory gameFactory = new GameFactory();
        GameExpert game = (GameExpert) gameFactory.create(4, GameMode.EXPERT);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.createCharacters();
        for (int i = 0; i < 3; i++) //Give enough coin for every possible character
            game.players.getByNickname("Simo").awardCoin();
        game.useCharacter("Simo", game.getDrawnCharacterIDs().get(0));
        assertEquals(game.getDrawnCharacterIDs().get(0), game.getActiveCharacterID(),
                "Active character should be the one selected");
        assertThrows(IllegalStateException.class, () -> game.useCharacter("Greg", game.getDrawnCharacterIDs().get(1)),
                "No other character should be able to get activated");
    }

    @RepeatedTest(5)
    void endOfRoundOperations() throws GameOverException {
        GameFactory gameFactory = new GameFactory();
        GameExpert game = (GameExpert) gameFactory.create(4, GameMode.EXPERT);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.createCharacters();
        for (int i = 0; i < 3; i++) //Give enough coin for every possible character
            game.players.getByNickname("Simo").awardCoin();
        game.useCharacter("Simo", game.getDrawnCharacterIDs().get(0));
        game.endOfRoundOperations();
        assertEquals(-1, game.getActiveCharacterID(),
                "Active character should be the one selected");
    }
}