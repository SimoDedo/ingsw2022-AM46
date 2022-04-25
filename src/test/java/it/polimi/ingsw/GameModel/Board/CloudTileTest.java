package it.polimi.ingsw.GameModel.Board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test for CloudTile. Most of the operations derived from StudentContainer are already tested
 */
public class CloudTileTest{

    /**
     * Test that clouds gets filled to expected value
     */
    @Test
    public void fill() {
        try {
            CloudTile cloudTile = new CloudTile(4, new Bag());
            cloudTile.fill();
            assertEquals(4, cloudTile.pawnCount(), "unexpected number of students in cloud after filling");
            CloudTile cloudTile1 = new CloudTile(3, new Bag());
            cloudTile1.fill();
            assertEquals(3, cloudTile1.pawnCount(), "unexpected number of students in cloud after filling");
        } catch (LastRoundException ignored) {}
    }

    /**
     * Test that students are correctly removed from cloud (also removing their StudentContainer), catches exception if already empty
     */
    @Test
    public void removeAll() {
        try {
            CloudTile cloudTile = new CloudTile(4, new Bag());
            cloudTile.fill();
            List<Student> students = cloudTile.removeAll();
            assertEquals(cloudTile.pawnCount(), 0, "students left in cloud after removeAll()");
            assertEquals(students.size(), 4, "unexpected number of students removed from cloud");
            for(Student s : students){
                assertNull(s.getStudentContainer(), "removed student still thinks he is in a cloud");
            }
            CloudTile cloudTile1 = new CloudTile(3, new Bag());
            cloudTile1.fill();
            students = cloudTile1.removeAll();
            assertEquals(cloudTile1.pawnCount(), 0, "students left in cloud after removeAll()");
            assertEquals(students.size(), 3, "unexpected number of students removed from cloud");
            assertNull(cloudTile1.removeAll(), "students were removed from empty cloud");
        } catch (LastRoundException ignored) {}

    }
}