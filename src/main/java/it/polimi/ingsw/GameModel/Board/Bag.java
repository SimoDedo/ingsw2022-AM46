package it.polimi.ingsw.GameModel.Board;


import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A special type of student container, from which students are drawn throughout the game.
 * This is the only source of students, that all the players draw from.
 */
public class Bag extends StudentContainer {
    public Bag() {
        super(null, 130);
        fillInitial();
    }

    /**
     * Function that fills the bag with 2 students of each color. Used for initial setup of the game.
     */
    private void fillInitial() {
        for (Color color : Color.values()) {
            for (int i = 0; i < 2; i++) {
                placePawn(new Student(color, this));
            }
        }
    }

    /**
     * Function that fills the bag with the remaining students (after the initial 10). Used for
     * initial setup of the game.
     */
    public void fillRemaining() {
        for (Color color : Color.values()) {
            for (int i = 0; i < 24; i++) {
                placePawn(new Student(color, this));
            }
        }
    }

    /**
     * Returns a list of students randomly removed from the bag.
     * @param numberToDraw number of students to draw
     * @return a list of the drawn students
     */
    public List<Student> drawN(int numberToDraw) {
        List<Student> drawnStudents = new ArrayList<>();
        int randomNum;
        for (int i = 0; i < numberToDraw; i++) {
            randomNum = ThreadLocalRandom.current().nextInt(0, pawnCount());
            drawnStudents.add(removePawnByIndex(randomNum));
        }
        return drawnStudents;
    }
}
