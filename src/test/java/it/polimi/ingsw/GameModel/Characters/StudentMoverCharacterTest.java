package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.Characters.CharacterFactory;
import it.polimi.ingsw.GameModel.Characters.StudentMoverCharacter;
import it.polimi.ingsw.Utils.Enum.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentMoverCharacterTest {

    @Test
    void testUseAbility() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        char7.useCharacter(new Player());
        List<Integer> parameterList = new ArrayList<>();
        char7.useAbility((list) -> {}, parameterList);
        assertEquals(char7.getUsesLeft(), 2);
    }

    @Test
    void testResetUseState() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        char7.useCharacter(new Player());
        List<Integer> parameterList = new ArrayList<>();
        char7.useAbility((list) -> {}, parameterList);
        char7.resetUseState();
        assertEquals(char7.getUsesLeft(), 3);
    }

    @Test
    void placePawn() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        Student student = new Student(Color.BLUE, char7);
        char7.placePawn(student);
        assertEquals(char7.getSize(), 1);
    }

    @Test
    void removePawn() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        Student student = new Student(Color.BLUE, char7);
        char7.placePawn(student);
        assertSame(student, char7.removePawn(student));
    }

    @Test
    void removePawnByIndex() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        Student student = new Student(Color.BLUE, char7);
        char7.placePawn(student);
        assertSame(student, char7.removePawnByIndex(0));
    }

    @Test
    void getPawnByID() {
        CharacterFactory factory = new CharacterFactory();
        StudentMoverCharacter char7 = (StudentMoverCharacter) factory.create(7);
        Student student = new Student(Color.BLUE, char7);
        char7.placePawn(student);
        assertSame(student, char7.getPawnByID(student.getID()));
    }
}