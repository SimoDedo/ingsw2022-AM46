package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class EntranceTest {
    @Test
    public void removeByIDTest(){
        List<Student> students = new ArrayList<>();
        List<Integer> studentIDs = new ArrayList<>();
        for(int i = 0; i < 9; i ++) {
            students.add(new Student(Color.PINK, null));
            studentIDs.add(students.get(students.size() - 1).getID());
        }
        Player p0 = new Player();
        Entrance e1 = new Entrance(p0, 9, 4, students);

        e1.removeStudentsByID(studentIDs.subList(0, 3));
        assert(e1.pawnCount() == 6);

    }



}
