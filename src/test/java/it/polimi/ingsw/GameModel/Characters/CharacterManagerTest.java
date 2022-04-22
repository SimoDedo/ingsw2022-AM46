package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC6;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC8;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC9;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyC2;
import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Character manager and for each of the Characters ability.
 * In order to test them, needed classes (players, archipelago, professorSet, ecc.) are created, thus simulating
 * a mock game. The creation and activation is the same for each test (barring the ID). Then a parameter list is
 * (correctly) constructed and the ability is activated. Finally, we assert that changes happened as expected.
 */
class CharacterManagerTest {

    // Note: strategies have to be tested separately, since they can be separated from the
    // characters' mechanics. already tested in fact always one step ahead

    /**
     * Test for C1. EFFECT: take 1 student from this card and place it on an island of your choice.
     * Then draw a new Student from the Bag and place it on this card.
     * We select a Student from the card and an IslandTile then ensure that it has been moved and
     * a new student was drawn onto the card
     */
    @Test
    void useC1() {
        int testingCharID = 1;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>();

        int studentFromCard = ((StudentMoverCharacter) manager.getCharacterByID(testingCharID)).getPawnIDs().get(0);  //Gets one student ID from card
        parameterList.add(studentFromCard);
        int islandTileDest = archipelago.getIslandTilesIDs().get(0).get(0); //Gets first island
        parameterList.add(islandTileDest);

        manager.useAbility(parameterList);

        assertEquals(studentFromCard, archipelago.getIslandTilesStudentsIDs().get(islandTileDest).get(0),
                "The only student in IslandTile destination should be the student selected");

        assertEquals(4, ((StudentMoverCharacter) manager.getCharacterByID(testingCharID)).getPawnIDs().size(),
                "After using the ability a new student is drawn");
    }

    /**
     * Test for C1 LastRoundException. Identical to useC1 except bag will be emptied, thus we both check that
     * the exception is thrown and that effect has taken place.
     */
    @Test
    void useC1LastRound() {
        int testingCharID = 1;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>();

        int studentFromCard = ((StudentMoverCharacter) manager.getCharacterByID(testingCharID)).getPawnIDs().get(0);  //Gets one student ID from card
        parameterList.add(studentFromCard);
        int islandTileDest = archipelago.getIslandTilesIDs().get(0).get(0); //Gets first island
        parameterList.add(islandTileDest);

        bag.drawN(bag.pawnCount()-1); //Remove all students except one from bag to trigger exception
        assertThrows(LastRoundException.class, () -> manager.useAbility(parameterList),
                "When the last student is drawn to be put on the card, the controller should receive LastRoundException");

        assertEquals(studentFromCard, archipelago.getIslandTilesStudentsIDs().get(islandTileDest).get(0),
                "The only student in IslandTile destination should be the student selected");

        assertEquals(4, ((StudentMoverCharacter) manager.getCharacterByID(testingCharID)).getPawnIDs().size(),
                "After using the ability a new student is drawn");
    }
    /**
     * Test for C2. EFFECT: activate the C2 modified professor strategy, giving it the activator
     * of C2 as parameter.
     * We ensure that the strategy was changed successfully with the right activator set on it.
     */
    @Test
    void useC2() {
        int testingCharID = 2;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>(); //unused for C2

        manager.useAbility(parameterList);

        assertInstanceOf(CheckAndMoveProfessorStrategyC2.class, professorSet.getCheckAndMoveProfessorStrategy(),
                "Should have changed the strategy after activation");
        assertEquals("Simo", ((CheckAndMoveProfessorStrategyC2)(professorSet.getCheckAndMoveProfessorStrategy())).getActivatingPlayer(),
                "Activating player should be the one who activated");
    }

    /**
     * Test for C3. EFFECT: immediate resolve call on an island of your choice.
     * We place a red student on the first Island and assign the red professor to the White team, then
     * ensure that the resolve has taken place.
     */
    @Test
    void useC3() {
        int testingCharID = 3;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        int islandTileDest = archipelago.getIslandTilesIDs().get(0).get(0); //IslandTile selected
        List<Integer> parameterList = new ArrayList<>();
        parameterList.add(islandTileDest);

        archipelago.placeStudent(new Student(Color.RED, null), islandTileDest); //Added a student to have meaningful resolve
        professorSet.setOwner(Color.RED, playerList.getByNickname("Pirovano"));

        manager.useAbility(parameterList);

        assertEquals(TowerColor.WHITE, archipelago.getTowerColorOfIslandGroup(0),
                "Team white should have won the resolved called by C3 ability");
    }

    /**
     * Test for C3 GameOverException. Identical to useC3 except all but one tower will be removed from one team, thus
     * we both check that the exception is thrown and that effect has taken place.
     */
    @Test
    void useC3GameOver() {
        int testingCharID = 3;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        int islandTileDest = archipelago.getIslandTilesIDs().get(0).get(0); //IslandTile selected
        List<Integer> parameterList = new ArrayList<>();
        parameterList.add(islandTileDest);

        for (int i = 0; i < 7; i++) //Remove all but one tower from White team in order to ensure game will end at next resolve
            playerList.getTowerHolder(TowerColor.WHITE).takeTower();


        archipelago.placeStudent(new Student(Color.RED, null), islandTileDest); //Added a student to have meaningful resolve
        professorSet.setOwner(Color.RED, playerList.getByNickname("Pirovano"));

        assertThrows(GameOverException.class, () -> manager.useAbility(parameterList),
                "Game should end since White team had all but one of their towers removed");

        assertEquals(TowerColor.WHITE, archipelago.getTowerColorOfIslandGroup(0),
                "Team white should have won the resolved called by C3 ability");
    }

    /**
     * Test for C4. EFFECT: activate the C4 modified moveMotherNature strategy which allows 2 additional
     * hops.
     * We ensure that the strategy was changed successfully.
     */
    @Test
    void useC4() {
        int testingCharID = 4;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet,coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>(); //unused for C4

        manager.useAbility(parameterList);

        assertInstanceOf(MoveMotherNatureStrategyC4.class, archipelago.getMoveMotherNatureStrategy(),
                "Should have changed the strategy after activation");
    }

    /**
     * Test for C5. EFFECT: sets up the NoEntryTilesSpace on an island of your choice.
     * We place a no entry tile and check it was placed, then place the remaining 3 and check.
     * Finally, check that resolving doesn't take place and tile is returned.
     */
    @Test
    void useC5() {
        int testingCharID = 5;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        int islandTileDest = archipelago.getIslandTilesIDs().get(0).get(0);
        List<Integer> parameterList = new ArrayList<>();
        parameterList.add(islandTileDest);

        manager.useAbility(parameterList);

        assertEquals(1, archipelago.getNoEntryTiles().get(0),
                "No entry tile should be placed");
        for (int i = 0; i < 3; i++) {
            manager.resetActiveCharacter();
            manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);
            manager.useAbility(parameterList);
        }
        assertEquals(4, archipelago.getNoEntryTiles().get(0),
                "Four no entry tile should be placed");

        archipelago.placeStudent(new Student(Color.RED, null), islandTileDest);
        professorSet.setOwner(Color.RED, playerList.getByNickname("Simo"));
        archipelago.resolveIslandGroup(0, playerList, professorSet);

        assertTrue(archipelago.getTowerColorOfIslandGroup(0) == null,
                "A no entry tile was placed, thus no resolving should take place");
        assertEquals(3, archipelago.getNoEntryTiles().get(0),
                "Three no entry tile should be placed, since one was used");
        assertEquals(1, ((NoEntryCharacter)manager.getCharacterByID(5)).getNoEntryTiles(),
                "Tile should be returned to character for later use");
    }

    /**
     * Test for C6. EFFECT: sets up the C6 modified resolve strategy, that doesn't count towers
     * when calculating influence.
     * We ensure that the strategy was changed successfully.
     */
    @Test
    void useC6() {
        int testingCharID = 6;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>(); //unused for C6

        manager.useAbility(parameterList);

        assertInstanceOf(ResolveStrategyC6.class, archipelago.getResolveStrategy(),
                "Should have changed the strategy after activation");
    }


    /**
     * Test for C7. EFFECT::In Setup, draw 6 Students and place them on this card. You may take up to 3 Students from
     * this card and replace them with the same number of students from your entrance.
     * We select a random student on the card and a random student in the activator's entrance. We then activate the ability and ensure
     * they have been swapped. We repeat the process a random amount of times (1 to 3) since user can choose how many times to do the operation.
     */
    @RepeatedTest(10)
    void useC7() {
        int testingCharID = 7;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>();

        for(int i = 0; i < ((new Random(System.currentTimeMillis())).nextInt(3)) ; i++){
            int studCard = ((StudentMoverCharacter)manager.getCharacterByID(7)).getPawnIDs().get(0);
            parameterList.add(studCard);
            int studEntrance = playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().get(0);
            parameterList.add(studEntrance);

            manager.useAbility(parameterList);

            assertTrue(((StudentMoverCharacter)manager.getCharacterByID(7)).getPawnIDs().contains(studEntrance),
                    "Card should now contain selected entrance student");
            assertFalse(((StudentMoverCharacter)manager.getCharacterByID(7)).getPawnIDs().contains(studCard),
                    "Card should no longer contain selected card student");
            assertTrue(playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().contains(studCard),
                    "Entrance should now contain selected card student");
            assertFalse(playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().contains(studEntrance),
                    "Entrance should no longer contain selected entrance student");
            parameterList.clear();
        }
    }

    /**
     * Test for C8. EFFECT: sets up the C8 modified resolve strategy, that adds 2 points of influence
     * to the activator.
     * We ensure that the strategy was changed successfully and the activator is the same as whoever used the character.
     */
    @Test
    void useC8() {
        int testingCharID = 8;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>(); //unused for C8

        manager.useAbility(parameterList);

        assertInstanceOf(ResolveStrategyC8.class, archipelago.getResolveStrategy(),
                "Should have changed the strategy after activation");
        assertEquals("Simo", ((ResolveStrategyC8)archipelago.getResolveStrategy()).getActivatingPlayer(),
                "The activator should be the player who used the character");
    }

    /**
     * Test for C9. EFFECT: sets up the C9 modified resolve strategy, that doesn't add influence
     * for the given color.
     * We ensure that the strategy was changed successfully and that the color was correctly set.
     */
    @Test
    void useC9() {
        int testingCharID = 9;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        List<Integer> parameterList = new ArrayList<>();
        parameterList.add(Arrays.stream(Color.values()).toList().indexOf(Color.RED));

        manager.useAbility(parameterList);

        assertInstanceOf(ResolveStrategyC9.class, archipelago.getResolveStrategy(),
                "Should have changed the strategy after activation");
        assertEquals(Color.RED, ((ResolveStrategyC9) archipelago.getResolveStrategy()).getColorToIgnore(),
                "The color to ignore should be the one selected");
    }

    /**
     * Test for C10. EFFECT: You may exchange up to 2 Students between your Entrance and your Dining Room.
     * We select a random student from entrance and save its color, then we select a random student in a table. After
     * activating the ability we check that the entrance contains the DiningRoomStudent and that the table of the color
     * saved contains the entrance student. We repeat this process 2 times changing the color of the DN student, since
     * it can be used up to 2 times.
     * The correct assignment of coin when using the ability is also tested.
     */
    @RepeatedTest(10)
    void useC10() throws FullTableException {
        int testingCharID = 10;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);


        List<Integer> parameterList = new ArrayList<>();

        for (int i = 0; i < 2; i++) { //Two uses
            playerList.getByNickname("Simo").addToDR(new Student(Color.values()[i], null)); //add a student to move

            int studEntrance = playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().get(0);
            Color tableDest = playerList.getByNickname("Simo").getEntranceStudentsIDs().get(studEntrance);
            parameterList.add(studEntrance);
            int studDN = playerList.getByNickname("Simo").getTableStudentsIDs(Color.values()[i]).get(0);
            parameterList.add(studDN);

            playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
            boolean coinGiven = playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
            int coinsBefore = playerList.getByNickname("Simo").getCoins();

            manager.useAbility(parameterList);

            assertTrue(playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().contains(studDN),
                    "Entrance should now contain selected DiningRoom student");
            assertFalse(playerList.getByNickname("Simo").getEntranceStudentsIDs().keySet().stream().toList().contains(studEntrance),
                    "Entrance should no longer contain selected entrance student");
            assertTrue(playerList.getByNickname("Simo").getTableStudentsIDs(tableDest).contains(studEntrance),
                    "DiningRoom should now contain selected entrance student");
            assertFalse(playerList.getByNickname("Simo").getTableStudentsIDs(Color.values()[i]).contains(studDN),
                    "DiningRoom should no longer contain selected DiningRoom student");
            if(!coinGiven)
                assertEquals(coinsBefore + 1, playerList.getByNickname("Simo").getCoins(),
                    "Because table already had 2 students placed, after activating the ability a coin should be awarded");
            parameterList.clear();
        }
    }

    /**
     * Test for C11. EFFECT: In Setup, draw 4 Students and place them on this card. Take 1 Student  from this card and
     * place it in your Dining Room. Then, draw a new Student from the Bag and place it on this card.
     * We select a random card student and save its color. We also add two students to the table of that color
     * Then after activating the ability, we ensure that the card no longer contains the selected student and that it
     * is instead contained in the table of the saved color. Finally, we ensure that a new student has been placed on
     * the card and that the player was correctly awarded a coin.
     */
    @Test
    void useC11() throws FullTableException {
        int testingCharID = 11;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);


        List<Integer> parameterList = new ArrayList<>();

        int studCard = ((StudentMoverCharacter)manager.getCharacterByID(11)).getStudentIDsAndColor().keySet().stream().toList().get(0);
        Color tableDest = ((StudentMoverCharacter)manager.getCharacterByID(11)).getStudentIDsAndColor().get(studCard);
        parameterList.add(studCard);

        playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
        playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
        int coinsBefore = playerList.getByNickname("Simo").getCoins();

        manager.useAbility(parameterList);

        assertFalse(((StudentMoverCharacter)manager.getCharacterByID(11)).getPawnIDs().contains(studCard),
                "Card should no longer contain selected card student");
        assertTrue(playerList.getByNickname("Simo").getTableStudentsIDs(tableDest).contains(studCard),
                "DiningRoom should contain selected card student");
        assertEquals(4, ((StudentMoverCharacter)manager.getCharacterByID(11)).getPawnIDs().size(),
                "A new student must be drawn");
        assertEquals(coinsBefore + 1, playerList.getByNickname("Simo").getCoins());
    }

    /**
     * Test for C11 LastRoundException. Identical to useC11 except bag will be emptied, thus we both check that
     * the exception is thrown and that effect has taken place.
     */
    @Test
    void useC11LastRound() throws FullTableException {
        int testingCharID = 11;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);


        List<Integer> parameterList = new ArrayList<>();

        int studCard = ((StudentMoverCharacter)manager.getCharacterByID(11)).getStudentIDsAndColor().keySet().stream().toList().get(0);
        Color tableDest = ((StudentMoverCharacter)manager.getCharacterByID(11)).getStudentIDsAndColor().get(studCard);
        parameterList.add(studCard);

        playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
        playerList.getByNickname("Simo").addToDR(new Student(tableDest, null));
        int coinsBefore = playerList.getByNickname("Simo").getCoins();

        bag.drawN(bag.pawnCount() - 1); //Draw all students except for one
        assertThrows(LastRoundException.class, () -> manager.useAbility(parameterList),
                "When the last student is drawn to be put on the card, the controller should receive LastRoundException");

        assertFalse(((StudentMoverCharacter)manager.getCharacterByID(11)).getPawnIDs().contains(studCard),
                "Card should no longer contain selected card student");
        assertTrue(playerList.getByNickname("Simo").getTableStudentsIDs(tableDest).contains(studCard),
                "DiningRoom should contain selected card student");
        assertEquals(4, ((StudentMoverCharacter)manager.getCharacterByID(11)).getPawnIDs().size(),
                "A new student must be drawn");
        assertEquals(coinsBefore + 1, playerList.getByNickname("Simo").getCoins());
    }

    /**
     * Test for C12. EFFECT: Choose a type of Student: every player (including youself) must return 3 Students of
     * that type from their Dining Room to the bag. If any player has fewer than 3 Students of that type,
     * return as many Students as they have.
     *
     */
    @Test
    void useC12() throws FullTableException {
        int testingCharID = 12;
        //Game components creation
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        ProfessorSet professorSet = new ProfessorSet();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);
        CoinBag coinBag = new CoinBag(20);
        //Players creation
        PlayerList playerList = new PlayerList();
        playerList.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        playerList.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));
        for (int i = 0; i < 15; i++) //Award some coins for later use
            playerList.getByNickname("Simo").awardCoin();
        //Character creation and usage
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet, coinBag);
        manager.createCharacter(testingCharID, bag);
        manager.useCharacter(playerList.getByNickname("Simo"), testingCharID);

        playerList.getByNickname("Simo").addToDR(new Student(Color.RED, null)); //Fill DN with students to then check if they were removed
        playerList.getByNickname("Simo").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Simo").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Simo").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Greg").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Greg").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Greg").addToDR(new Student(Color.RED, null));
        playerList.getByNickname("Pirovano").addToDR(new Student(Color.RED, null));

        List<Integer> parameterList = new ArrayList<>();
        parameterList.add(Arrays.stream(Color.values()).toList().indexOf(Color.RED));

        int bagContentBefore = bag.pawnCount();
        manager.useAbility(parameterList);

        assertEquals(1, playerList.getByNickname("Simo").getTableStudentsIDs(Color.RED).size(),
                "Player Simo should have one red student left in his DN");
        assertEquals(0, playerList.getByNickname("Greg").getTableStudentsIDs(Color.RED).size(),
                "Player Simo should have one red student left in his DN");
        assertEquals(0, playerList.getByNickname("Pirovano").getTableStudentsIDs(Color.RED).size(),
                "Player Simo should have one red student left in his DN");
        assertEquals(0, playerList.getByNickname("Ceruti").getTableStudentsIDs(Color.RED).size(),
                "Player Simo should have one red student left in his DN");
        assertEquals(bagContentBefore + 7, bag.pawnCount(),
                "In this scenario exactly 7 student should be added back in the bag");
    }
}