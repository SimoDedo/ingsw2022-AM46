package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

public class BagTest{

    @Test
    public void testFillRemaining() {
        Bag bag = new Bag();
        bag.fillRemaining();
        assertEquals(130, bag.pawnCount());
    }

    @Test
    public void testDrawN() {
        Bag bag = new Bag();
        List<Student> students = bag.drawN(10);
        assertTrue(students.size() == 10 && bag.pawnCount() == 0);
    }


}