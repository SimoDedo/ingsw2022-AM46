package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentMoverCharacterTest {

    @Test
    void testUseAbility() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        char7.useCharacter(new Player());
        List<Integer> parameterList = new ArrayList<>();
        char7.useAbility((list) -> {}, parameterList);
        assertEquals(char7.getUsesLeft(), 2);
    }

    @Test
    void testResetUseState() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        char7.useCharacter(new Player());
        List<Integer> parameterList = new ArrayList<>();
        char7.useAbility((list) -> {}, parameterList);
        char7.resetUseState();
        assertEquals(char7.getUsesLeft(), 3);
    }

    @Test
    void placePawn() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        Student student = new Student(Color.BLUE, char7);
        char7.removePawnByID(char7.getPawnIDs().get(0));
        char7.placePawn(student);
        assertEquals(6, char7.getSize());
    }

    @Test
    void removePawn() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        Student student = new Student(Color.BLUE, char7);
        char7.removePawnByID(char7.getPawnIDs().get(0));
        char7.placePawn(student);
        char7.removePawn(student);
        assertEquals(5, char7.getSize());
    }

    @Test
    void removePawnByID() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        Student student = new Student(Color.BLUE, char7);
        char7.removePawnByID(char7.getPawnIDs().get(0));
        assertEquals(5, char7.getSize());
    }

    @Test
    void getPawnByID() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7, new Bag());
        Student student = new Student(Color.BLUE, char7);
        char7.removePawnByID(char7.getPawnIDs().get(0));
        char7.placePawn(student);
        assertSame(student, char7.getPawnByID(student.getID()));
    }
}