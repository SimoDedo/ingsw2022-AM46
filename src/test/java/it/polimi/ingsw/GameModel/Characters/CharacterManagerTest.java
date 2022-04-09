package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.Characters.CharacterManager;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharacterManagerTest {

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
        List parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        manager.useAbility(parameterList);
        //todo: asserts
    }

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
        List parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        manager.useAbility(parameterList);
        //todo: asserts
    }

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
        List parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        manager.useAbility(parameterList);
        //todo: asserts
    }

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
        List parameterList = new ArrayList<>();

        manager.createCharacter(testingCharID);
        manager.useCharacter(rightPlayer, testingCharID);
        //todo: build parameter list
        manager.useAbility(parameterList);
        //todo: asserts
    }

}