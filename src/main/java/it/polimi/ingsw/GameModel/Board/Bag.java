package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.ArrayList;

/**
 * A special type of student container, from which students are drawn throughout the game.
 * This is the only source of students, that all the players draw from.
 */
public class Bag extends StudentContainer{
    public Bag() {
        super(null, 130);
        fillInitial();
    }

    /**
     * Function that fills the bag with 2 students of each color. Used for initial setup of the game.
     */
    private void fillInitial() {
        for (Color c : Color.values()) {
            placePawn(new Student(c, this));
            placePawn(new Student(c, this));
        }
    }

    public void fillRemaining() {
        //todo
    }

    public ArrayList<Student> drawN(int numberToDraw) {
        ArrayList<Student> drawnStudents = new ArrayList<>();
        //todo
        return drawnStudents;
    }
}
