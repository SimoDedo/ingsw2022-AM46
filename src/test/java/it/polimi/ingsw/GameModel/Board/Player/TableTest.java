package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TableTest {

    /**
     * Tests that students get placed and score gets correctly calculated
     */
    @Test
    public void placeStudentTest() throws FullTableException{
        List<Student> studentList = new ArrayList<>();
        Student studentToPlace = new Student(Color.PINK, null);
        for (int i = 0; i < 5; i++) {
            studentList.add(new Student(Color.PINK, null));
        }
        Table table = new Table(new Player(), 10, Color.PINK);
        for (Student student : studentList){
            table.placeStudent(student);
        }
        table.placeStudent(studentToPlace);
        assertThrows(IllegalArgumentException.class, () -> table.placeStudent(studentToPlace));
        assertThrows(IllegalArgumentException.class, () -> table.placeStudent(new Student(Color.RED, null)));
        assertTrue(table.getScore() == 6);

    }
}
