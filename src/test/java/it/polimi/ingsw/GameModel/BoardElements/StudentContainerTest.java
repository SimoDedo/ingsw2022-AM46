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
        IslandTile islandTile1 = new IslandTile(null, -1, true, null);
        islandTile1.placePawn(new Student(Color.GREEN, null));
        islandTile1.placePawn(new Student(Color.RED, null));
        islandTile1.placePawn(new Student(Color.BLUE, null));
        assertTrue(islandTile1.getPawns().size() == 3);
    }

    /**
     * Tests that removePawn actually removes pawns inside container. It checks by counting the pawns before and after removal
     */
    @Test
    void removePawn() {
        IslandTile islandTile1 = new IslandTile(null, -1, true, null);
        Student studentToRemove = new Student(Color.GREEN, null);
        islandTile1.placePawn(studentToRemove);
        islandTile1.placePawn(new Student(Color.RED, null));
        islandTile1.placePawn(new Student(Color.BLUE, null));
        assertTrue(islandTile1.getPawns().size() == 3 && studentToRemove.getStudentContainer().equals(islandTile1));
        islandTile1.removePawn(studentToRemove);
        assertTrue(islandTile1.getPawns().size() == 2 && studentToRemove.getStudentContainer().equals(null));
    }

    /**
     * Tests moveStudent method
     */
    @Test
    void moveStudent(){
        IslandTile islandTile1 = new IslandTile(null, -1, true, null);
        IslandTile islandTile2 = new IslandTile(null, -1, true, null);
        islandTile1.moveStudent(new Student(Color.GREEN, null));
        Student studentToMove = new Student(Color.PINK, null);
        islandTile2.moveStudent(studentToMove);
        islandTile1.moveStudent(studentToMove);
        assertTrue(studentToMove.getStudentContainer().equals(islandTile1) && islandTile1.countInfluence(Color.PINK) == 1 && islandTile2.countInfluence(Color.PINK) == 0);
    }

    /**
     * Tests that a PawnContainer either returns null if it does not contain a Pawn with such ID or the correct student
     */
    @Test
    void getPawnByID(){
        IslandTile islandTile = new IslandTile(null, -1, true, null);
        islandTile.moveStudent(new Student(Color.GREEN, null));
        islandTile.moveStudent(new Student(Color.PINK, null));
        islandTile.moveStudent(new Student(Color.RED, null));
        islandTile.moveStudent(new Student(Color.BLUE, null));
        islandTile.moveStudent(new Student(Color.YELLOW, null));
        assertTrue(islandTile.getPawnByID(0) == null && islandTile.getPawnByID(5).getColor().equals(Color.YELLOW));
    }
}