package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

public class DiningRoomTest {

    @Test
    public void testConstructor(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        for(Color c : Color.values()) {
            assert (dr.getTable(c) != null);
        }
    }

    @Test
    public void placeStudentTest(){
        Player p0 = new Player();
        DiningRoom dr = new DiningRoom(p0);
        Student s0= new Student(Color.PINK, null), s1= new Student(Color.PINK, null), s2 = new Student(Color.PINK, null);
        try {
            dr.placeStudent(s0);
            dr.placeStudent(s1);
            dr.placeStudent(s2);
        } catch (FullTableException e){assert(false);}
        assert(dr.getScore(Color.PINK) == 3);

    }
}
