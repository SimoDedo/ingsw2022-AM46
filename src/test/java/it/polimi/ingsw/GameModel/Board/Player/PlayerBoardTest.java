package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayerBoardTest {

    /**
     * Test that checks that the player board is created correctly, tables can be accessed
     * and students from the entrance are removed correctly from the PlayerBoard class.
     */
    @Test
    public void constructorTest(){
        PlayerConfig playerConfig = new PlayerConfig(2);
        Bag bag = new Bag();
        List<Integer> IDList = bag.getPawnIDs();

        playerConfig.setBag(bag);
        PlayerBoard board = new PlayerBoard(new Player(), TowerColor.BLACK, true, playerConfig);

        assertNotNull(board.getTable(Color.BLUE), "blue table was not found");
        int matches = 0, discarded = 0;
        for (int ID : IDList) {
            try {
                board.removeStudentByID(ID);
                matches++;
            } catch (NoSuchElementException ignored) {
                discarded++;
            }
        }
        assertEquals(matches, 7, "one or more students in the entrance were not found");
        assertEquals(discarded, 3, "the wrong amount of students was placed in the entrance");

    }

    /**
     * Tests removal, placements and lookup of students to and from the dining room and the entrance.
     */
    @Test
    public void moveStudentsFromEntranceToDiningRoomTest(){
        PlayerConfig playerConfig = new PlayerConfig(2);
        Bag bag = new Bag();
        bag.fillRemaining();
        playerConfig.setBag(bag);
        Player p0 = new Player();




        PlayerBoard board = new PlayerBoard(p0, TowerColor.BLACK, true, playerConfig);

        for(Color c : Color.values()){
            assertEquals(board.getScore(c), 0);
        }

        List<Integer> studentsIDs = new ArrayList<>(board.getEntranceStudentsIDs().keySet());

        assertEquals(studentsIDs.size(), 7);

        for(int id : studentsIDs){

            Student student = board.removeStudentByID(id);
            assertThrows(NoSuchElementException.class, () -> board.getStudentByID(id), "Student was not removed properly from entrance");
            assertThrows(NoSuchElementException.class, () -> board.removeStudentByID(id), "Student was not removed properly from entrance");
            try {
                board.addToDiningRoom(student);
            } catch (FullTableException e) { fail("table is improperly signaling that it is full"); }
            assertThrows(NoSuchElementException.class, () -> board.getStudentFromEntrance(id), "Student was not removed properly from entrance");
            assertEquals(board.getTableStudentsIDs(student.getColor()).get(0), id, "Student was not placed in dining room, or cant be found");
            try{
                board.getStudentByID(id);
                board.removeStudentByID(id);
            } catch (NoSuchElementException e) { fail("Student was not found in DiningRoom, where it is supposed to be"); }
            assertThrows(NoSuchElementException.class, () -> board.removeStudentByID(id), "Student was not removed properly from diningRoom");


        }
    }


}
