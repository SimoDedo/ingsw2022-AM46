package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test most Game methods.
 * It will act as if it was the controller, giving "commands" to make the state of the game change. Should simulate game flow.
 * It will then observe the Model to ensure method correctly functions.
 */
class GameTest {

    /**
     * Tests that wizards are correctly assigned
     */
    @Test
    void assignWizard() {
        GameFactory gameFactory = new GameFactory();
        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        Game game = gameFactory.create(4, GameMode.NORMAL, teamConfiguration);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        assertThrows(IllegalArgumentException.class, () -> game.assignWizard("Greg", WizardType.KING));
        assertTrue(game.getPlayerWizardType().get("Greg").equals(WizardType.WITCH));
    }

    @Test
    void determineFirstRoundOrder() {//Tested with TurnManager
    }

    @Test
    void getCurrentPlayer() {//Just a getter
    }

    /**
     * Test that user can only play the right assistant, and that it is correctly placed in the cards played
     * @throws LastRoundException not tested
     */
    @Test
    void playAssistant() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        Game game = gameFactory.create(4, GameMode.NORMAL, teamConfiguration);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.playAssistant("Simo", 4);
        game.playAssistant("Greg", 6);
        game.playAssistant("Pirovano", 3);
        game.playAssistant("Ceruti", 8);

        assertTrue(game.getCardPlayedThisRound().get("Simo").equals(4));
        assertThrows(IllegalArgumentException.class, () -> game.playAssistant("Ceruti", 4));
        game.pushThisRoundInLastRound();
        assertTrue(game.getCardPlayedLastRound().get("Simo").equals(4));
        assertThrows(NoSuchElementException.class, () -> game.playAssistant("Simo", 4));
    }

    @Test
    void moveStudentFromEntrance() {
    }

    @Test
    void checkAndMoveProfessor() {
    }

    @Test
    void moveMotherNature() {
    }

    @Test
    void resolveIslandGroup() {
    }

    @Test
    void takeFromCloud() {
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