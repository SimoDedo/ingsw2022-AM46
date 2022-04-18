package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test most Game methods.
 * It will mostly act as if it was the controller, giving "commands" to make the state of the game change.
 * Tests will somewhat simulate game flow when situations are simple.
 * Otherwise, (such as when a whole match should be simulated) protected Game attributes are directly modified to
 * achieve a specific game state, in order to avoid complex logic that simulates long matches. It should be noted that
 * the controller won't see those attributes and will only interact with the model through public methods.
 * It will then observe the Model to ensure method correctly functions.
 */
class GameTest {

    @Test
    void playerTest(){
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.NORMAL);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        assertThrows(IllegalArgumentException.class, () -> game.createPlayer("Pirovano", TowerColor.BLACK));
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);
    }

    /**
     * Tests that wizards are correctly assigned.
     * Creates a mock game situation and assigns each Player a wizard as if they were selected.
     */
    @Test
    void assignWizard() {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.NORMAL);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        assertThrows(IllegalArgumentException.class, () -> game.assignWizard("Greg", WizardType.KING),
                "Shouldn't be able to select an already selected mage type");
        assertEquals(WizardType.WITCH ,game.getPlayersWizardType().get("Greg"),
                "Player 'Greg' should be assigned Witch mage type");
    }

    /**
     * Test that user can only play the right assistant, and that it is correctly placed in the cards played.
     * First we test that players can't use same assistant, then we test that they can't play
     * already played assistant.
     */
    @RepeatedTest(10)
    void playAssistant() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.NORMAL);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.determineFirstRoundOrder();
        game.playAssistant("Simo", 4);
        game.playAssistant("Greg", 6);
        game.playAssistant("Pirovano", 3);
        game.playAssistant("Ceruti", 8);

        assertEquals(4, (int) game.getCardsPlayedThisRound().get("Simo"),
                "Player should have played card number 4");
        assertThrows(IllegalArgumentException.class, () -> game.playAssistant("Ceruti", 4),
                "Player can't play assistant already played by another");
        game.pushThisRoundInLastRound();
        assertEquals(4, (int) game.getCardsPlayedLastRound().get("Simo"),
                "Player should have played card number 4 last round");
        assertThrows(NoSuchElementException.class, () -> game.playAssistant("Simo", 4),
                "Player can't play assistant already played in preceding rounds");
    }

    /**
     * Tests that user whose only cards left have already been played this round (i.e. desperate) can play any
     * card left, even ones who were already played this round.
     * Ensures this can only happen when user is actually desperate.
     */
    @Test
    void playAssistantDesperate() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.NORMAL);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.determineFirstRoundOrder();
        for (int i = 1; i < 8; i++) {
            game.playAssistant("Simo", i); //Player will only have left cards 8,9,10; Others will play all those cards
        }
        game.pushThisRoundInLastRound();
        game.playAssistant("Greg", 10);
        game.playAssistant("Pirovano", 9);

        assertThrows(IllegalArgumentException.class, () -> game.playAssistant("Simo", 10),
                "Player should not play card 10 since he has a non played card (card 8 here) in his hand");

        game.playAssistant("Ceruti", 8);
        game.playAssistant("Simo", 10);

        assertEquals(10, (int) game.getCardsPlayedThisRound().get("Simo"),
                "Player Greg should have played card number 10");
        assertEquals(10, (int) game.getCardsPlayedThisRound().get("Simo"),
                "Player Simo should have played card number 10 even if greg already did, since he is desperate");
    }

    /**
     * Tests that with correct input a Student is successfully moved from the Entrance to the Table.
     */
    @Test
    void moveStudentFromEntranceToDN() {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.NORMAL);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        int studentToMove = game.getEntranceStudentsIDs("Simo").keySet().stream().toList().get(0); //Gets random student in entrance
        Color color = game.getEntranceStudentsIDs("Simo").get(studentToMove);   //Gets the student color, to know which table to move it to
                                                                                        // (This could be automatic! no need for player to select table,
                                                                                        //ID retrieved automatically)
        int table = game.getTableIDs("Simo").get(color);
        game.moveStudentFromEntrance("Simo", studentToMove, table);

        assertTrue(game.getTableStudentsIDs("Simo", color).contains(studentToMove),
                "Table should contain Student moved");
        assertFalse(game.getEntranceStudentsIDs("Simo").containsKey(studentToMove),
                "Entrance should no longer contain Student moved");
    }

    @Test
    void determinePlanningOrder() {
    }

    @Test
    void determineActionOrder() {
    }

    @Test
    void pushThisRoundInLastRound() {
    }

    @Test
    void refillClouds() {
    }

    @Test
    void disableClouds() {
    }

    @Test
    void endOfRoundOperations() {
    }
}