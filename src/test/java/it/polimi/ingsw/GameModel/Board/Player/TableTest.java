package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

public class TableTest {

    @Test
    public void placeStudentTest(){

        Student studentToPlace = new Student(Color.PINK, null);
        Table table = new Table(new Player(), 10, Color.PINK);
        for(int i = 0; i < 5; i ++) {
            try{ table.placeStudent(studentToPlace); } catch (FullTableException e){assert(false);}
        }
        assert(table.getScore() == 5);

    }
}
