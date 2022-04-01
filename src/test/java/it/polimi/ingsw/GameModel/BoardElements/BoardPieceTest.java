package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BoardPieceTest {

    /**
     * Tests that global indexes are given correctly
     */
    @Test
    public void getMaxID(){
        Student student0 = new Student(Color.PINK, null);
        Student student1 = new Student(Color.PINK, null);
        Student student2 = new Student(Color.PINK, null);
        Professor professor = new Professor(null, Color.PINK);
        assertTrue(student0.getID() == 0 && professor.getID() == 3);
    }

}