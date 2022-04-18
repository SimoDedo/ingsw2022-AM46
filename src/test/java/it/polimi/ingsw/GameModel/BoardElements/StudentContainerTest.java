package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.Utils.Enum.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the abstract class StudentContainer using IslandTile. This class doesn't override the methods, so it's testing the implementation common to all StudentContainers
 */
class StudentContainerTest {

    /**
     * Tests that placePawn actually places pawns inside container. It checks by counting the pawns
     */
    @Test
    void placePawn() {
        IslandTile islandTile1 = new IslandTile(null, true, null);
        islandTile1.placePawn(new Student(Color.GREEN, null));
        islandTile1.placePawn(new Student(Color.RED, null));
        islandTile1.placePawn(new Student(Color.BLUE, null));
        assertEquals(3, islandTile1.getPawns().size(), "unexpected number of students in island tile");
    }

    /**
     * Tests that removePawn actually removes pawns inside container. It checks by counting the pawns before and after removal
     */
    @Test
    void removePawn() {
        IslandTile islandTile1 = new IslandTile(null, true, null);
        Student studentToRemove = new Student(Color.GREEN, null);
        islandTile1.placePawn(studentToRemove);
        islandTile1.placePawn(new Student(Color.RED, null));
        islandTile1.placePawn(new Student(Color.BLUE, null));
        assertEquals(islandTile1.getPawns().size(), 3, "unexpected number of students in island tile");
        assertEquals(studentToRemove.getStudentContainer(), islandTile1, "student in island thinks he is somewhere else");
        islandTile1.removePawn(studentToRemove);
        assertEquals(2, islandTile1.getPawns().size(), "unexpected number of students in island tile after removal");
        assertNull(studentToRemove.getStudentContainer(), "student removed from island thinks he is still there");
    }

    /**
     * Tests moveStudent method
     */
    @Test
    void moveStudent(){
        IslandTile islandTile1 = new IslandTile(null, true, null);
        IslandTile islandTile2 = new IslandTile(null, true, null);
        islandTile1.moveStudent(new Student(Color.GREEN, null));
        Student studentToMove = new Student(Color.PINK, null);
        islandTile2.moveStudent(studentToMove);
        islandTile1.moveStudent(studentToMove);
        assertEquals(studentToMove.getStudentContainer(), islandTile1, "student moved to island1 thinks he is somewhere else");
        assertEquals(1, islandTile1.countInfluence(Color.PINK), "unexpected number of pink students in destination island after move");
        assertEquals(0, islandTile2.countInfluence(Color.PINK), "unexpected number of pink students in source island after move");
    }

    /**
     * verifies that the ID list contains students actually contained by the islandTile
     */
    @Test
    void  getPawnsIDs(){
        IslandTile islandTile = new IslandTile(null, true, null);
        islandTile.moveStudent(new Student(Color.GREEN, null));
        islandTile.moveStudent(new Student(Color.PINK, null));
        islandTile.moveStudent(new Student(Color.RED, null));
        islandTile.moveStudent(new Student(Color.BLUE, null));
        Student studentToTest = new Student(Color.YELLOW, null);
        islandTile.moveStudent(studentToTest);
        assertTrue(islandTile.getPawnIDs().contains(studentToTest.getID()), "student in island was not found in its id list");
        assertEquals(5, islandTile.getPawnIDs().size(), "unexpected number of student ids in island");
    }

    /**
     * Tests that a PawnContainer either returns null if it does not contain a Pawn with such ID or the correct student
     */
    @Test
    void getPawnByID(){
        IslandTile islandTile = new IslandTile(null, true, null);
        islandTile.moveStudent(new Student(Color.GREEN, null));
        islandTile.moveStudent(new Student(Color.PINK, null));
        islandTile.moveStudent(new Student(Color.RED, null));
        islandTile.moveStudent(new Student(Color.BLUE, null));
        Student studentToTest = new Student(Color.YELLOW, null);
        islandTile.moveStudent(studentToTest);
        assertEquals(islandTile.getPawnByID(studentToTest.getID()).getColor(), Color.YELLOW, "unexpected color of student found by ID");
        assertNull(islandTile.getPawnByID(99), "non existing student was found");
    }
}