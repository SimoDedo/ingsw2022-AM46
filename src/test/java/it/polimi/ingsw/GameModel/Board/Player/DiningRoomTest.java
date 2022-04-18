package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class DiningRoomTest {

    /**
     * Verifies that tables are created properly (one per color) + getter
     */
    @Test
    public void testConstructor(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        for(Color c : Color.values()) {
            assertNotNull(dr.getTable(c), String.format("Table of color %s was not found", c.toString()));
        }
    }

    /**
     * Verifies that students are automatically placed in correct table and further getter testing
     */
    @Test
    public void placeStudentTest(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        Student s0 = new Student(Color.PINK, null), s1= new Student(Color.PINK, null), s2 = new Student(Color.PINK, null), s3 = new Student(Color.PINK, null), s4= new Student(Color.PINK, null), s5 = new Student(Color.PINK, null), s6 = new Student(Color.PINK, null), s7= new Student(Color.PINK, null), s8 = new Student(Color.PINK, null), s9 = new Student(Color.PINK, null), s10= new Student(Color.PINK, null);
        Student s11 = new Student(Color.RED, null), s12= new Student(Color.GREEN, null), s13 = new Student(Color.BLUE, null);
        try {
            dr.placeStudent(s0);
            dr.placeStudent(s1);
            dr.placeStudent(s2);
            dr.placeStudent(s3);
            dr.placeStudent(s4);
            dr.placeStudent(s5);
            dr.placeStudent(s6);
            dr.placeStudent(s7);
            dr.placeStudent(s8);
            assertThrows(IllegalArgumentException.class, ()->dr.placeStudent(s8), "this student is already in a table");
            dr.placeStudent(s9);
            dr.placeStudent(s11);
            dr.placeStudent(s12);
            dr.placeStudent(s13);

        } catch (FullTableException e){fail("tables are not full, should be able to accept more students");}
        assertEquals(dr.getScore(Color.PINK), 10, "got wrong number of pink students");
        assertEquals(dr.getScore(Color.GREEN), 1, "got wrong number of green students");

        assertThrows(FullTableException.class, ()->dr.placeStudent(s10),
                "table is full, should not accept any more (pink) students");

    }

    /**
     * Testing the getByID method
     */
    @Test
    public void getStudentByIDTest(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        Student s0 = new Student(Color.PINK, null), s1= new Student(Color.RED, null), s2 = new Student(Color.GREEN, null);
        int id0 = s0.getID(), id1 = s1.getID(), id2 = s2.getID();

        try {
            dr.placeStudent(s0);
            dr.placeStudent(s1);
            dr.placeStudent(s2);

        } catch (FullTableException e){fail("tables are not full, should be able to accept more students");}

        assertEquals(s0, dr.getStudentByID(id0), "getStudentByID has returned the wrong student (ID not corresponding)");
        assertEquals(s1, dr.getStudentByID(id1), "getStudentByID has returned the wrong student (ID not corresponding)");
        assertEquals(s2, dr.getStudentByID(id2), "getStudentByID has returned the wrong student (ID not corresponding)");
        assertThrows(NoSuchElementException.class, () -> dr.getStudentByID(900));
        dr.removeStudentByID(id0);
        assertThrows(NoSuchElementException.class, () -> dr.getStudentByID(id0));

    }
}
