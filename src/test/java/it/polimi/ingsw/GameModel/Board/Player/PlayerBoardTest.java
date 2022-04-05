package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayerBoardTest {

    @Test
    public void constructorTest(){
        PlayerConfig playerConfig = new PlayerConfig(2);
        Bag bag = new Bag();
        bag.fillRemaining();
        playerConfig.setBag(bag);
        int maxStudents = 7;
        Player p0 = new Player();
        List<Integer> studentIDs = new ArrayList<>();


        PlayerBoard boardTest = new PlayerBoard(p0, TowerColor.BLACK, true, playerConfig);

        for(int ID : studentIDs){assert (boardTest.getStudentByID(ID) != null);}
        assertThrows(NoSuchElementException.class, ()-> boardTest.getStudentByID(maxStudents * 2));
    }

    @Test
    public void studentsArrayToIslandTest(){
        PlayerConfig playerConfig = new PlayerConfig(2);
        Bag bag = new Bag();
        bag.fillRemaining();
        playerConfig.setBag(bag);
        int maxStudents = 7;
        Player p0 = new Player();
        List<Student> initialEntrance = new ArrayList<>();
        List<Integer> studentIDs = new ArrayList<>();

        for(int i = 0; i < 3; i ++){
            Student student = new Student(Color.PINK, null);
            initialEntrance.add(student);
            studentIDs.add(student.getID());
        }
        for(int i = 0; i < 4; i ++){
            Student student = new Student(Color.BLUE, null);
            initialEntrance.add(student);
            studentIDs.add(student.getID());
        }


        PlayerBoard board = new PlayerBoard(p0, TowerColor.BLACK, true, playerConfig);
        HashMap<Integer, Integer> movements = new HashMap<>();
        for( int i = 0; i < maxStudents; i++){
            movements.put(studentIDs.get(i), i+1);
        }
        for(Color c : Color.values()){
            assert  (board.getScore(c) == 0);
        }
    }


}
