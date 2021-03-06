package it.polimi.ingsw.Controller;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.Utils.Enum.WizardType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the game controller. They test that the controller state correctly evolves in response
 * to given UserAction. It won't test that messages (info or error alike) are sent, but will test
 * the controller state (which implies message sending).
 */
class ControllerTest {

    /**
     * Tests that first user connecting is expected to give game settings, while others are directly expected
     * to choose their assistant.
     */
    @Test
    void gameJoinTest() {
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo")); //First player login

        assertEquals(UserActionType.GAME_SETTINGS, controller.getExpectedUserAction().get("Simo"),
                "After his login, the first player is expected to choose his tower color");

        controller.receiveUserAction(new GameSettingsUserAction("Greg", 3, GameMode.EXPERT));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg")); //Second player login

        assertEquals(UserActionType.TOWER_COLOR, controller.getExpectedUserAction().get("Simo"),
                "After selecting the game mode, the first player is expected to choose his tower color");
        assertEquals(UserActionType.TOWER_COLOR, controller.getExpectedUserAction().get("Greg"),
                "Any other player is immediately expected to choose his tower color");
    }

    /**
     * Tests that when a selection error should be sent the controller state doesn't change, expecting the same
     * action that caused the error, and that when a correct choice was given the controller correctly expects
     * the next action
     */
    @Test
    void setupErrorTest(){
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo"));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg"));
        controller.receiveUserAction(new TowerColorUserAction("Simo", TowerColor.BLACK));
        assertEquals(UserActionType.WIZARD, controller.getExpectedUserAction().get("Simo"),
                "After correct choice, player is expected next user action");

        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.BLACK)); //Incorrect choice for 2 player game
        assertEquals(UserActionType.TOWER_COLOR, controller.getExpectedUserAction().get("Greg"),
                "Player who made illegal choice is expected to choose again");

        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.WHITE)); //Correct choice
        assertEquals(UserActionType.WIZARD, controller.getExpectedUserAction().get("Greg"),
                "After correct choice, player is expected next user action");
    }

    /**
     * Tests that setup is successful if actions are taken correctly.
     */
    @Test
    void setupTest(){
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo"));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg"));
        controller.receiveUserAction(new TowerColorUserAction("Simo", TowerColor.BLACK)); //Tower color choosing
        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.WHITE));
        controller.receiveUserAction(new WizardUserAction("Simo", WizardType.MAGE)); //Mage choosing

        assertNull(controller.getExpectedUserAction().get("Simo"),
                "Player who successfully chose wizard will be waiting for his turn.");

        controller.receiveUserAction(new WizardUserAction("Greg", WizardType.SAMURAI)); //After last player chooses wizard, game starts

        assertEquals(1, controller.getExpectedUserAction().size(),
                "Only one player should be expected to take any action");
        assertTrue(controller.getExpectedUserAction().containsValue(UserActionType.PLAY_ASSISTANT),
                "Player should be expected to play assistant as his first move");
    }

    /**
     * Tests that playing assistant correctly changes expected action according to cards played.
     */
    @Test
    void playAssistantTest(){
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo"));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg"));
        controller.receiveUserAction(new TowerColorUserAction("Simo", TowerColor.BLACK)); //Tower color choosing
        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.WHITE));
        controller.receiveUserAction(new WizardUserAction("Simo", WizardType.MAGE)); //Mage choosing
        controller.receiveUserAction(new WizardUserAction("Greg", WizardType.SAMURAI));

        ObservableByClient game = controller.getGame();
        String firstPlayer = game.getCurrentPlayer();
        controller.receiveUserAction(new PlayAssistantUserAction(firstPlayer, 4));

        assertNull(controller.getExpectedUserAction().get(firstPlayer),
                "After playing its card, the first player is waiting his turn.");

        controller.receiveUserAction(new PlayAssistantUserAction(game.getCurrentPlayer(), 5));

        assertEquals(UserActionType.MOVE_STUDENT, controller.getExpectedUserAction().get(firstPlayer),
                "The first player (who played the lowest card) is expected to move a student");
        assertEquals(1, controller.getExpectedUserAction().size(),
                "Only one player should be expected to take any action");
    }

    /**
     * Tests that action phase operations can be sequentially taken without fail.
     */
    @Test
    void actionPhaseTest(){
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo"));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg"));
        controller.receiveUserAction(new TowerColorUserAction("Simo", TowerColor.BLACK)); //Tower color choosing
        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.WHITE));
        controller.receiveUserAction(new WizardUserAction("Simo", WizardType.MAGE)); //Mage choosing
        controller.receiveUserAction(new WizardUserAction("Greg", WizardType.SAMURAI));
        ObservableByClient game = controller.getGame();
        String firstPlayer = game.getCurrentPlayer();
        controller.receiveUserAction(new PlayAssistantUserAction(firstPlayer, 4));
        controller.receiveUserAction(new PlayAssistantUserAction(game.getCurrentPlayer(), 5));

        for (int i = 0; i < 3; i++) {
            assertEquals(UserActionType.MOVE_STUDENT, controller.getExpectedUserAction().get(firstPlayer),
                    "The first player (who played the lowest card) is expected to move student number " + (i+1));
            int studID = game.getEntranceStudentsIDs(firstPlayer).keySet().stream().toList().get(0);
            int islandID = game.getIslandTilesIDs().values().stream().toList().get(0).get(0);
            controller.receiveUserAction(new MoveStudentUserAction(firstPlayer, studID, islandID));
        }
        assertEquals(UserActionType.MOVE_MOTHER_NATURE, controller.getExpectedUserAction().get(firstPlayer),
                "The first player, when all students were moved, is expected to move mother nature");

        int mnIsland = game.getMotherNatureIslandTileID();
        int destinationIG = game.getIslandTilesIDs().entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(mnIsland))
                .findAny() //always present
                .get()//always present
                .getKey();
        destinationIG = destinationIG == 11 ? 0 : destinationIG + 1;
        int destinationIT = game.getIslandTilesIDs().get(destinationIG).get(0);

        controller.receiveUserAction(new MoveMotherNatureUserAction(firstPlayer ,destinationIT));
        assertEquals(UserActionType.TAKE_FROM_CLOUD, controller.getExpectedUserAction().get(firstPlayer),
                "The first player, after moving mother nature, is expected choose a cloud");

        int cloudId = game.getCloudIDs().get(0);
        controller.receiveUserAction(new TakeFromCloudUserAction(firstPlayer, cloudId));
        assertEquals(UserActionType.END_TURN, controller.getExpectedUserAction().get(firstPlayer),
                "The first player, after taking from cloud, is expected end its turn");
        controller.receiveUserAction(new EndTurnUserAction(firstPlayer));
        assertNull(controller.getExpectedUserAction().get(firstPlayer),
                "The first player, after ending its turn, is not expected to do an action");
    }

    /**
     * Tests that when a round ends it correctly notifies the next phase and that the correct player is
     * expected to play the next assistant
     */
    @Test
    void endOfRoundTest(){
        Controller controller = new Controller();
        controller.receiveUserAction(new LoginUserAction("Simo"));
        controller.receiveUserAction(new GameSettingsUserAction("Simo", 2, GameMode.NORMAL));
        controller.receiveUserAction(new LoginUserAction("Greg"));
        controller.receiveUserAction(new TowerColorUserAction("Simo", TowerColor.BLACK)); //Tower color choosing
        controller.receiveUserAction(new TowerColorUserAction("Greg", TowerColor.WHITE));
        controller.receiveUserAction(new WizardUserAction("Simo", WizardType.MAGE)); //Mage choosing
        controller.receiveUserAction(new WizardUserAction("Greg", WizardType.SAMURAI));
        ObservableByClient game = controller.getGame();
        String firstPlayer = game.getCurrentPlayer();
        controller.receiveUserAction(new PlayAssistantUserAction(firstPlayer, 4));
        controller.receiveUserAction(new PlayAssistantUserAction(game.getCurrentPlayer(), 5));
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(UserActionType.MOVE_STUDENT, controller.getExpectedUserAction().get(game.getCurrentPlayer()),
                        "The first player (who played the lowest card) is expected to move student number " + (i+1));
                int studID = game.getEntranceStudentsIDs(game.getCurrentPlayer()).keySet().stream().toList().get(0);
                int islandID = game.getIslandTilesIDs().values().stream().toList().get(0).get(0);
                controller.receiveUserAction(new MoveStudentUserAction(game.getCurrentPlayer(), studID, islandID));
            }
            assertEquals(UserActionType.MOVE_MOTHER_NATURE, controller.getExpectedUserAction().get(game.getCurrentPlayer()),
                    "The first player, when all students were moved, is expected to move mother nature");

            int mnIsland = game.getMotherNatureIslandTileID();
            int destinationIG = game.getIslandTilesIDs().entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().contains(mnIsland))
                    .findAny()
                    .get()//always present
                    .getKey();
            destinationIG = destinationIG == 11 ? 0 : destinationIG + 1;
            int destinationIT = game.getIslandTilesIDs().get(destinationIG).get(0);

            controller.receiveUserAction(new MoveMotherNatureUserAction(game.getCurrentPlayer() ,destinationIT));

            int cloudId = game.getCloudIDs().get(i);
            controller.receiveUserAction(new TakeFromCloudUserAction(game.getCurrentPlayer(), cloudId));

            controller.receiveUserAction(new EndTurnUserAction(game.getCurrentPlayer()));
        }

        assertEquals(UserActionType.PLAY_ASSISTANT, controller.getExpectedUserAction().get(firstPlayer),
                "After round is over, next planning phase should start with player who played lowest assistant");

    }



}