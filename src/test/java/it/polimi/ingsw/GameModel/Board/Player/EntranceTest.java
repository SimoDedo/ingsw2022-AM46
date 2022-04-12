package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EntranceTest {

    @Test
    public void fillInitialTest() {
        Bag bag = new Bag();
        bag.fillRemaining();
        Player p0 = new Player();
        Entrance entrance = new Entrance(p0, 10, 4, bag);


        assertEquals(entrance.pawnCount(), 10);
    }

    @Test
    public void removeStudentsTest(){

        Bag bag = new Bag();
        bag.fillRemaining();
        Player p0 = new Player();
        Entrance entrance = new Entrance(p0, 10, 4, bag);
        List<Integer> pawnIDs = entrance.getPawnIDs();
        assertEquals(pawnIDs.size(), 10);
        entrance.removeStudentsByID(pawnIDs.subList(0, 4));
        assertEquals(entrance.pawnCount(), 6);
    }

}
