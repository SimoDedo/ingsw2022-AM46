package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public void removeStudentsTest(){

        Bag bag = new Bag();
        bag.fillRemaining();
        Player p0 = new Player();
        Entrance entrance = new Entrance(p0, 10, 4, bag);
        Set<Integer> pawnIDs = entrance.getPawnIDs();
        assertEquals(pawnIDs.size(), 10);
        entrance.removeStudentsByID(pawnIDs);
        assertEquals(entrance.pawnCount(), 6);
    }

}
