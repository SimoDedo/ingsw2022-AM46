package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the Table class.
 */
public class TableTest {

    /**
     * Tests that students get placed and score gets correctly calculated
     */
    @Test
    public void placeStudentTest() throws FullTableException{
        List<Student> studentList = new ArrayList<>();
        Student studentToPlace = new Student(Color.PINK, null);
        for (int i = 0; i < 8; i++) {
            studentList.add(new Student(Color.PINK, null));
        }
        Table table = new Table(new Player(), 10, Color.PINK);
        for (Student student : studentList){
            table.placeStudent(student);
        }
        table.placeStudent(studentToPlace);
        assertThrows(IllegalArgumentException.class, () -> table.placeStudent(studentToPlace),
                "table allowed placing a student which it already contained");
        assertEquals(table.getScore(), 9, "wrong number of students present in the table");
        table.placeStudent(new Student(Color.PINK, null));
        assertThrows(FullTableException.class, () -> table.placeStudent(new Student(Color.PINK, null)),
                "table is allowing to place more students than its maximum amount");


    }
}
