package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.Characters.CharacterManager;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Character manager and for each of the Characters.
 */
class CharacterManagerTest {

    // Note: strategies have to be tested separately, since they can be separated from the
    // characters' mechanics.

    /**
     * Test for C1. EFFECT: take 1 student from this card and place it on an island of your choice.
     * Then draw a new Student from the Bag and place it on this card.
     */
    @Test
    void useC1() {
        int testingCharID = 1;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);
        List<Integer> parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        // manager.useAbility(parameterList);
        // asserts
    }

    /**
     * Test for C2. EFFECT: activate the C2 modified professor strategy, giving it the activator
     * of C2 as parameter.
     */
    @Test
    void useC2() {
        int testingCharID = 2;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);
        List<Integer> parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: manager.useAbility(parameterList);
        // asserts
    }

    /**
     * Test for C3. EFFECT: immediate resolve call on an island of your choice.
     */
    @Test
    void useC3() {
        int testingCharID = 3;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);
        List<Integer> parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        // manager.useAbility(parameterList);
        // asserts
    }

    /**
     * Test for C4. EFFECT: activate the C4 modified moveMotherNature strategy which allows 2 additional
     * hops.
     */
    @Test
    void useC4() {
        int testingCharID = 4;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        manager.useAbility(new ArrayList<>());
        assertSame(manager.getCharacterByID(testingCharID).getOwner(), rightPlayer);
        assertSame(archipelago.getMoveMotherNatureStrategy().getClass(), MoveMotherNatureStrategyC4.class);
    }

    /**
     * Test for C5. EFFECT: sets up the NoEntryTilesSpace on an island of your choice.
     */
    @Test
    void useC5() {
        int testingCharID = 5;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);
        List<Integer> parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        // manager.useAbility(parameterList);
        // asserts
    }

    /**
     * Test for C6. EFFECT: sets up the C6 modified resolve strategy, that doesn't count towers
     * when calculating influence.
     */
    @Test
    void useC6() {
        int testingCharID = 5;
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        PlayerList playerList = new PlayerList();
        Player rightPlayer = new Player();
        playerList.add(rightPlayer);
        playerList.add(new Player());
        playerList.add(new Player());
        playerList.add(new Player());
        ProfessorSet professorSet = new ProfessorSet();
        CharacterManager manager = new CharacterManager(archipelago, bag, playerList, professorSet);
        List<Integer> parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: manager.useAbility(parameterList);
        // asserts
    }

}