package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiningRoomTest {

    @Test
    public void testConstructor(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        for(Color c : Color.values()) {
            assert (dr.getTable(c) != null);
        }
    }

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
            assertThrows(IllegalArgumentException.class, ()->dr.placeStudent(s8));
            dr.placeStudent(s9);
            dr.placeStudent(s11);
            dr.placeStudent(s12);
            dr.placeStudent(s13);

        } catch (FullTableException e){fail();}
        assertEquals(dr.getScore(Color.PINK), 10);
        assertEquals(dr.getScore(Color.GREEN), 1);

        assertThrows(FullTableException.class, ()->dr.placeStudent(s10));

    }

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

        } catch (FullTableException e){fail();}

        assertEquals(s0, dr.getStudentByID(id0));
        assertEquals(s1, dr.getStudentByID(id1));
        assertEquals(s2, dr.getStudentByID(id2));

    }
}
