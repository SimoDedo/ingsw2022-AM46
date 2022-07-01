package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for the Bag class.
 */
public class BagTest{

    /**
     * checks that the bag fills properly (correct number of students)
     */
    @Test
    public void testFillRemaining() {
        Bag bag = new Bag();
        bag.fillRemaining();
        assertEquals(130, bag.pawnCount(), "unexpected number of students in bag after fill");
    }

    /**
     * checks that students are drawn from the bag as expected
     */
    @Test
    public void testDrawN() {
        Bag bag = new Bag();
        List<Student> students = bag.drawN(10);
        assertEquals(students.size(),10, "unexpected number of students drawn");
        assertEquals(bag.pawnCount(), 0, "unexpected number of students left in bag");
    }


}