package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;
import java.util.*;

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
        Game game = gameFactory.create(4, GameMode.STANDARD);
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
        Game game = gameFactory.create(4, GameMode.STANDARD);
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
        Game game = gameFactory.create(4, GameMode.STANDARD);
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
        Game game = gameFactory.create(4, GameMode.STANDARD);
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
    void moveStudentFromEntranceToDN() throws FullTableException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
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

    /**
     * Tests that with correct input a Student is successfully moved from the Entrance to the IslandTile.
     */
    @Test
    void moveStudentFromEntranceToIslandTIle() throws FullTableException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        int studentToMove = game.getEntranceStudentsIDs("Simo").keySet().stream().toList().get(0);
        int island = game.getIslandTilesIDs().get(0).get(0); //Gets first IslandTile of first IslandGroup
        game.moveStudentFromEntrance("Simo", studentToMove, island);

        assertTrue(game.getIslandTilesStudentsIDs().get(island).contains(studentToMove),
                "Island should contain Student moved");
        assertFalse(game.getEntranceStudentsIDs("Simo").containsKey(studentToMove),
                "Entrance should no longer contain Student moved");
    }

    /**
     * Tests that professor is assigned to the person with the most students after one is moved to the Dining Room.
     * A mock game is created and a random student is moved in the table of a player.
     * Said player who should own the professor of the student color.
     * Then a student is moved from the entrance of another player. If said student is of another color, the second
     * player will own the professor o said color.
     * Otherwise, since it would be a tie, the professor shouldn't change ownership.
     */
    @RepeatedTest(10)
    void checkAndMoveProfessor() throws FullTableException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        int studentToMove = game.getEntranceStudentsIDs("Simo").keySet().stream().toList().get(0);
        Color color = game.getEntranceStudentsIDs("Simo").get(studentToMove);
        int table = game.getTableIDs("Simo").get(color);
        game.moveStudentFromEntrance("Simo", studentToMove, table);

        assertEquals("Simo", game.getProfessorsOwner().get(color),
                "Professor should be owned by whoever holds the most students after moving to DN");

        studentToMove = game.getEntranceStudentsIDs("Greg").keySet().stream().toList().get(0);
        Color color2 = game.getEntranceStudentsIDs("Greg").get(studentToMove);
        table = game.getTableIDs("Greg").get(color2);
        game.moveStudentFromEntrance("Greg", studentToMove, table);

        if(!color.equals(color2))
            assertEquals("Greg", game.getProfessorsOwner().get(color2),
                    "Professor should be owned by whoever holds the most students after moving to DN");
        else
            assertEquals("Simo", game.getProfessorsOwner().get(color2),
                    "In case of a tie, the professor should not change ownership");
    }

    /**
     * Tests that mother nature is correctly placed on destination IslandTile, given that the inputs are valid.
     * First we retrieve the IslandTile which contains MN, then we retrieve its IslandGroup.
     * Then we move MN to the next IslandGroup (always a legal move) and check if it actually contains MN
     */
    @RepeatedTest(10)
    void moveMotherNature() throws LastRoundException, GameOverException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.playAssistant("Simo", 10);

        int islandTileMN = game.getMotherNatureIslandTileID();
        int destinationIG = game.getIslandTilesIDs().entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(islandTileMN))
                .findAny()
                .get()//always present
                .getKey();
        destinationIG = destinationIG == 11 ? 0 : destinationIG + 1;

        int destinationIT = game.getIslandTilesIDs().get(destinationIG).get(0);
        game.moveMotherNature("Simo", destinationIT);
        assertEquals(destinationIT, game.getMotherNatureIslandTileID(),
                "Mother nature should be placed in tile that was chosen");
    }

    /**
     * Tests that when MotherNature is moved to an IslandTile containing students it gets resolved.
     * First a mock game is created and a random student is moved to the IslandGroup next to that of MN
     * Then MN is moved to the next IslandGroup, which will be resolved and hold Towers
     */
    @RepeatedTest(10)
    void resolveIslandGroup() throws GameOverException, LastRoundException, FullTableException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.playAssistant("Simo", 10);

        List<Integer> studentsToMove = new ArrayList<>();
        int tableID = 0;
        for(Color color : Color.values()){
            long count = game.getEntranceStudentsIDs("Simo").entrySet().stream()
                    .filter(e -> e.getValue() == color)
                    .count();
            if(count > 1){
                studentsToMove = game.getEntranceStudentsIDs("Simo").entrySet().stream()
                        .filter(en -> en.getValue() == color)
                        .toList()
                        .stream()
                        .map(ent -> ent.getKey())
                        .toList();
                tableID = game.getTableIDs("Simo").get(color);
                break;
            }
        }
        //Select random student from entrance along with their color (thus an entry)
        int islandTileMN = game.getMotherNatureIslandTileID();
        int destinationIG = game.getIslandTilesIDs().entrySet().
                stream().
                filter(entry -> entry.getValue().contains(islandTileMN)).
                findAny().
                get(). //always present
                        getKey();
        destinationIG = destinationIG == 11 ? 0 : destinationIG + 1;

        int island = game.getIslandTilesIDs().get(destinationIG).get(0);
        game.moveStudentFromEntrance("Simo", studentsToMove.get(0), tableID);
        game.moveStudentFromEntrance("Simo", studentsToMove.get(1), island);

        game.moveMotherNature("Simo", island);
        assertEquals(TowerColor.BLACK, game.getIslandGroupsOwners().get(destinationIG),
                "The island should be conquered by team who owns the only student placed");
    }

    /**
     * Tests that, once 3 students were moved from the entrance, the player is able to take students from a Cloud.
     */
    @RepeatedTest(10)
    void takeFromCloud() throws LastRoundException, FullTableException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        for (int i = 0; i < 3; i++) {
            Map.Entry<Integer, Color> studentToMove = game.getEntranceStudentsIDs("Simo").entrySet().stream().toList().get(0);
            game.moveStudentFromEntrance("Simo",
                    studentToMove.getKey(),
                    game.getTableIDs("Simo").get(studentToMove.getValue()));
        }
        assertEquals(4, game.getEntranceStudentsIDs("Simo").keySet().size(),
                "After moving the students only 4 should be left in the entrance");

        game.refillClouds();
        game.takeFromCloud("Simo", game.getCloudIDs().get(0));
        assertEquals(7, game.getEntranceStudentsIDs("Simo").keySet().size(),
                "After taking from cloud entrance should be full");
        assertEquals(0, game.getCloudStudentsIDs(game.getCloudIDs().get(0)).size(),
                "After taking from cloud, cloud should be empty");
    }

    /**
     * Tests that when the planning order is computed it correctly uses 'clockwise' order along with cards played.
     * First a game is created, we than choose the first order and play some assistants.
     * Then we 'complete' the action phase and check that planning order is correct
     */
    @RepeatedTest(10)
    void determinePlanningOrder() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.determineFirstRoundOrder();
        game.playAssistant("Simo", 7);
        game.playAssistant("Greg", 9);
        game.playAssistant("Pirovano", 1);
        game.playAssistant("Ceruti", 8);

        game.determineActionOrder();
        game.nextPhase();
        game.determinePlanningOrder();
        game.nextPhase();
        assertEquals("Pirovano", game.getPlayerOrder().get(0),
                "First player in planning phase should be who played lowest card");
        assertTrue(game.getPlayerOrder().get(1).equals("Ceruti")&&
                        game.getPlayerOrder().get(2).equals("Simo") &&
                        game.getPlayerOrder().get(3).equals("Greg"),
                "Order of other player should follow 'clockwise' order (order of connection)");
    }

    /**
     * Tests that when the action order is computed it correctly uses the card played.
     * First a game is created, we than choose the first order and play some assistants.
     * Finally, we compute and check the action order.
     */
    @RepeatedTest(10)
    void determineActionOrder() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.determineFirstRoundOrder();
        game.playAssistant("Simo", 7);
        game.playAssistant("Greg", 9);
        game.playAssistant("Pirovano", 1);
        game.playAssistant("Ceruti", 8);

        game.determineActionOrder();
        game.nextPhase();
        assertTrue(game.getPlayerOrder().get(0).equals("Pirovano") &&
                        game.getPlayerOrder().get(1).equals("Simo")&&
                        game.getPlayerOrder().get(2).equals("Ceruti") &&
                        game.getPlayerOrder().get(3).equals("Greg"),
                "Player order should follow lowest to highest card played");
    }

    /**
     * Tests that when this round gets pushed, the last round is correctly filled and this round is now empty.
     */
    @Test
    void pushThisRoundInLastRound() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.assignWizard("Simo", WizardType.MAGE);
        game.assignWizard("Greg", WizardType.WITCH);
        game.assignWizard("Pirovano", WizardType.KING);
        game.assignWizard("Ceruti", WizardType.SAMURAI);

        game.determineFirstRoundOrder();
        game.playAssistant("Simo", 7);
        game.playAssistant("Greg", 9);
        game.playAssistant("Pirovano", 1);
        game.playAssistant("Ceruti", 8);
        game.pushThisRoundInLastRound();
        assertEquals(7, (int) game.getCardsPlayedLastRound().get("Simo"),
                "Should contain card played last round");
        assertEquals(0, game.getCardsPlayedThisRound().size(),
                "Should no longer contain previous round cards");
    }

    /**
     * Checks that a randomly chosen cloud is correctly emptied after method call
     */
    @RepeatedTest(10)
    void disableClouds() throws LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.refillClouds();
        game.disableClouds();
        assertEquals(0, game.getCloudStudentsIDs(game.getCloudIDs().get((new Random()).nextInt(4))).size(),
                "Any cloud should now be empty");
    }

    //region Win condition

    /**
     * Tests that method determineWinner correctly returns winning player when a team has no more towers.
     * It also ensures that the GameOverException is thrown when the condition is met.
     * Each island gets a red student placed and the red professor is assigned to a black player.
     * 8 island are resolved, so that 8 towers get placed and the winner is checked.
     */
    @Test
    void determineWinnerNoMoreTowers() throws GameOverException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        for (int i = 0; i < 12; i++) {
            game.archipelago.placeStudent(new Student(Color.RED, null),
                    game.getIslandTilesIDs().get(i).get(0));
        }

        game.professorSet.setOwner(Color.RED, game.players.getByNickname("Simo"));
        game.resolveIslandGroup(0);
        for (int i = 0; i < 6; i++)
            game.resolveIslandGroup(1); //each time merges with 0, so always next to check 1
        assertThrows(GameOverException.class, () -> game.resolveIslandGroup(1),
                "When last tower is placed GameOverException is thrown");
        assertEquals(TowerColor.BLACK, game.determineWinner(),
                "The winner should be the team who placed all towers");
        assertEquals(0, game.getTowersLeft(TowerColor.BLACK),
                "The winner should not have any towers left");
    }

    /**
     * Tests that method determine winner correctly returns winning player when three islands are formed.
     * It also ensures that the GameOverException is thrown when the condition is met.
     * First 5 islands get 2 red students placed, next 3 get 2 blue ones and last 4 get 2 pink ones.
     * Each player gets a professor, and all islands are resolved so that only 3 groups remain.
     * The winner should be the player who owns the red professor.
     */
    @Test
    void determineWinnerThreeIslands() throws GameOverException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(3, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.WHITE);
        game.createPlayer("Pietro", TowerColor.GREY);

        for (int i = 0; i < 5; i++){
            game.archipelago.placeStudent(new Student(Color.RED, null), game.getIslandTilesIDs().get(i).get(0));
            game.archipelago.placeStudent(new Student(Color.RED, null), game.getIslandTilesIDs().get(i).get(0));
        }
        for (int i = 5; i < 8; i++){
            game.archipelago.placeStudent(new Student(Color.BLUE, null), game.getIslandTilesIDs().get(i).get(0));
            game.archipelago.placeStudent(new Student(Color.BLUE, null), game.getIslandTilesIDs().get(i).get(0));
        }
        for (int i = 8; i < 12; i++){
            game.archipelago.placeStudent(new Student(Color.PINK, null), game.getIslandTilesIDs().get(i).get(0));
            game.archipelago.placeStudent(new Student(Color.PINK, null), game.getIslandTilesIDs().get(i).get(0));
        }


        game.professorSet.setOwner(Color.RED, game.players.getByNickname("Simo"));
        game.professorSet.setOwner(Color.BLUE, game.players.getByNickname("Greg"));
        game.professorSet.setOwner(Color.PINK, game.players.getByNickname("Pietro"));

        game.resolveIslandGroup(0);
        for (int i = 0; i < 4; i++)
            game.resolveIslandGroup(1);
        game.resolveIslandGroup(1);
        for (int i = 0; i < 2; i++)
            game.resolveIslandGroup(2);
        game.resolveIslandGroup(2);
        for (int i = 0; i < 2; i++)
            game.resolveIslandGroup(3);

        assertThrows(GameOverException.class, () -> game.resolveIslandGroup(3),
                "When three islands are formed GameOverException is thrown");
        assertEquals(TowerColor.BLACK, game.determineWinner(),
                "The winner should be the team who placed the most towers");
        assertEquals(1, game.getTowersLeft(TowerColor.BLACK),
                "In this scenario the winner should have 1 towers left (not 0)");
    }

    /**
     * Tests that method determine winner correctly returns winning player when a player has no cards left.
     * Method should correctly throw LastRoundException when a player has played its last card. The exception
     * lets the controller know this will be the last round, so it can change the game state with setLastRound.
     * When endOfRoundOperation is called at the end of the round, since it was notified to be the last, we expect
     * a GameOverException to be thrown. In this simple scenario the winner will be the black team.
     */
    @Test
    void determineWinnerNoCards() throws GameOverException, LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.archipelago.placeStudent(new Student(Color.RED, null), game.getIslandTilesIDs().get(0).get(0));
        game.archipelago.placeStudent(new Student(Color.PINK, null), game.getIslandTilesIDs().get(0).get(0));
        game.professorSet.setOwner(Color.RED, game.players.getByNickname("Simo"));
        game.professorSet.setOwner(Color.PINK, game.players.getByNickname("Greg"));
        game.professorSet.setOwner(Color.BLUE, game.players.getByNickname("Pirovano"));
        game.resolveIslandGroup(0);

        game.assignWizard("Simo", WizardType.MAGE);
        for (int i = 1; i < 10; i++) {
            game.playAssistant("Simo", i);
        }

        assertThrows(LastRoundException.class, () -> game.playAssistant("Simo", 10),
                "When last card is played LastRoundException needs to be thrown");
        game.setLastRound(); //Operation done by controller when receiving Exception above
        assertThrows(GameOverException.class, () -> game.endOfRoundOperations(),
                "At the end of the round game should end since it is the last round");
        assertEquals(TowerColor.BLACK, game.determineWinner(),
                "Winner should be team black");
    }

    /**
     * Tests that method determine winner correctly returns winning player when students are over.
     * RefillClouds should correctly throw LastRoundException when last student was drawn. The exception
     * lets the controller know this will be the last round, so it can change the game state with setLastRound and
     * disable clouds.
     * When endOfRoundOperation is called at the end of the round, since it was notified to be the last, we expect
     * a GameOverException to be thrown. In this simple scenario the winner will be the black team.
     */
    @RepeatedTest(20)
    void determineWinnerNoStudents() throws GameOverException, LastRoundException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.archipelago.placeStudent(new Student(Color.RED, null), game.getIslandTilesIDs().get(0).get(0));
        game.professorSet.setOwner(Color.RED, game.players.getByNickname("Simo"));
        game.resolveIslandGroup(0);

        game.bag.drawN((new Random()).nextInt(13) + 108); //Leaves random number of students in bag [0,12]
        game.assignWizard("Simo", WizardType.MAGE);
        for (int i = 1; i < 10; i++) {
            game.playAssistant("Simo", i);
        }

        assertThrows(LastRoundException.class, () -> game.refillClouds(),
                "When all students have been drawn LastRoundException should be thrown");
        game.setLastRound(); //Operation done by controller when receiving Exception above
        game.disableClouds(); //Operation done by controller when receiving Exception above
        assertEquals(0, game.getCloudStudentsIDs(game.getCloudIDs().get((new Random().nextInt(4)))).size(),
                "Any random cloud should be empty (disabled)");
        assertThrows(GameOverException.class, () -> game.endOfRoundOperations(),
                "At the end of the round game should end since it is the last round");
        assertEquals(TowerColor.BLACK, game.determineWinner(),
                "Winner should be team black");
    }

    /**
     * Tests that method determine winner correctly returns winning player when three islands are formed.
     * It also ensures that the GameOverException is thrown when the condition is met.
     * First 5 islands get 2 red students placed, next 3 get 2 blue ones and last 4 get 2 pink ones.
     * Each player gets a professor, and all islands are resolved so that only 3 groups remain.
     * The winner should be the player who owns the red professor.
     */
    @Test
    void determineWinnerTowerTie() throws GameOverException {
        GameFactory gameFactory = new GameFactory();
        Game game = gameFactory.create(4, GameMode.STANDARD);
        game.createPlayer("Simo", TowerColor.BLACK);
        game.createPlayer("Greg", TowerColor.BLACK);
        game.createPlayer("Pirovano", TowerColor.WHITE);
        game.createPlayer("Ceruti", TowerColor.WHITE);

        game.archipelago.placeStudent(new Student(Color.RED, null), game.getIslandTilesIDs().get(0).get(0));
        game.archipelago.placeStudent(new Student(Color.BLUE, null), game.getIslandTilesIDs().get(0).get(0));
        game.archipelago.placeStudent(new Student(Color.PINK, null), game.getIslandTilesIDs().get(1).get(0));
        game.archipelago.placeStudent(new Student(Color.GREEN, null), game.getIslandTilesIDs().get(1).get(0));


        game.professorSet.setOwner(Color.RED, game.players.getByNickname("Simo"));
        game.professorSet.setOwner(Color.BLUE, game.players.getByNickname("Greg"));
        game.professorSet.setOwner(Color.PINK, game.players.getByNickname("Pirovano"));
        game.professorSet.setOwner(Color.GREEN, game.players.getByNickname("Ceruti"));
        game.professorSet.setOwner(Color.YELLOW, game.players.getByNickname("Simo"));

        game.resolveIslandGroup(0);
        game.resolveIslandGroup(1);

        assertEquals(TowerColor.BLACK, game.determineWinner(),
                "In case of tie in towers placed, team with more professors win");
    }

    //endregion

}