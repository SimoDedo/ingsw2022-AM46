package it.polimi.ingsw.GameModel.Board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test for CloudTile. Most of the operations derived from StudentContainer are already tested
 */
public class CloudTileTest{

    /**
     * Test that initial fill at creation and later fill completely fill the bag with all 130 students
     */
    @Test
    public void fill() {
        try {
            CloudTile cloudTile = new CloudTile(4, new Bag());
            cloudTile.fill();
            assertTrue(cloudTile.pawnCount() == 4);
            CloudTile cloudTile1 = new CloudTile(3, new Bag());
            cloudTile1.fill();
            assertTrue(cloudTile1.pawnCount() == 3);
        }catch (GameOverException e){}
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
            assertTrue(cloudTile.pawnCount() == 0 && students.size() == 4 && students.get(0).getStudentContainer() == null);
            CloudTile cloudTile1 = new CloudTile(3, new Bag());
            cloudTile1.fill();
            students = cloudTile1.removeAll();
            assertTrue(cloudTile1.pawnCount() == 0 && students.size() == 3);
            assertThrows(IllegalStateException.class,() -> cloudTile1.removeAll());
        } catch (GameOverException e){}

    }
}