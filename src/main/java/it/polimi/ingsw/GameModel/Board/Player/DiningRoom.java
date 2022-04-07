package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that models the container for the five tables on the player board.
 */
public class DiningRoom {
    private List<Table> tables = new ArrayList<>();

    /**
     * Constructor for the Dining Room that initializes the tables, giving them the player as
     * their owner.
     *
     * @param player the owner of the tables
     */
    public DiningRoom(Player player) {
        for(Color c : Color.values()) tables.add(new Table(player, 10, c));
    }

    /**
     * Getter for the Table with the given Color.
     *
     * @param color the color of the table to find
     * @return the table with the given color
     */
    public Table getTable(Color color) {
        for(Table t : tables) {
            if(t.getColor() == color) return t;
        }
        return null;
    }

    /**
     * Method that calculates the score of the Table with the given Color.
     *
     * @param color the color of the table of which to calculate the score
     * @return the score of the table
     */
    public int getScore(Color color) {
        for(Table t : tables) {
            if(t.getColor() == color) return t.getScore();
        }
        return 0;
    }

    /**
     * Method that places a Student in the Table of its Color. Calls placeStudent method in Table.
     * @param student student to place inside the dining room
     * @throws FullTableException when the table is already full
     */
    public void placeStudent(Student student) throws FullTableException {
        for(Color c : Color.values()) {
            if(student.getColor() == c) getTable(c).placeStudent(student);
        }
    }

    /**
     * Method that returns the Student in the DiningRoom with the given ID.
     *
     * @param ID the ID of the student to retrieve
     * @return the student with the specified ID
     * @throws NoSuchElementException if this DiningRoom does not contain the specified student
     */
    public Student getStudentByID(int ID) throws NoSuchElementException {
        Student s = null;
        for (Table t : tables) {
            try {
                s = t.getPawnByID(ID);
                break; // is this break statement needed?
            } catch (NoSuchElementException ignored) {}
        }
        if (s == null) { throw new NoSuchElementException(); }
        return s;
    }



}
