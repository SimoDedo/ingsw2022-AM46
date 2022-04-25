package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EntranceTest {

    /**
     * Verifies that the entrance has the correct number of students after it is initialized
     */
    @Test
    public void fillInitialTest() {
        Bag bag = new Bag();
        bag.fillRemaining();
        Player p0 = new Player();
        Entrance entrance = new Entrance(p0, 10, 4, bag);


        assertEquals(entrance.pawnCount(), 10, "wrong number of students in entrance");
    }

    /**
     * Verifies that the removal process works wits IDs
     */
    @Test
    public void removeStudentsTest(){

        Bag bag = new Bag();
        bag.fillRemaining();
        Player p0 = new Player();
        Entrance entrance = new Entrance(p0, 10, 4, bag);
        List<Integer> pawnIDs = entrance.getPawnIDs();
        assertEquals(pawnIDs.size(), 10, "incorrect number of IDs supplied by getPawnIDs method");
        entrance.removeStudentsByID(pawnIDs.subList(0, 4));
        assertEquals(entrance.pawnCount(), 6, "incorrect number of students has been removed (possibly none at all)");
    }

}
