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
        new Student(Color.PINK, null);
        new Student(Color.PINK, null);
        Professor professor = new Professor(null, Color.PINK);
        assertEquals(student0.getID(), professor.getID() - 3, "IDs have not been assigned sequentially");
    }

}