package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Entrance extends StudentContainer {

    /**
     * how many students can be moved from the entrance each round, and how many have to be put back
     */
    private final int movableStudents;

    /**
     * reference to Bag, shared between all entrances
     */
    private final Bag bag;

    /**
     * @param player the owner of the board (is it necessary?)
     * @param maxPawns number of students that are initially placed in the entrance
     * @param movableStudents number of students to move during each action phase
     */
    public Entrance(Player player, int maxPawns, int movableStudents, Bag bag)
            throws IllegalStateException {
        super(player, maxPawns);
        this.movableStudents = movableStudents;
        this.bag = bag;
        fillInitial();
    }

    /**
     * Fills pawns List with students at the start of the game
     */
    private void fillInitial() {
        placePawns(bag.drawN(this.getMaxPawns()));
    }


    /**
     * potentially useful... unused for now
     *
     * @param IDList IDs of the students to remove from the entrance
     * @return the list of removed Students
     * @throws NoSuchElementException if there is at least one ID with no match
     * @throws IllegalArgumentException if the size of the ID list is incorrect for the game type
     */
    public List<Student> removeStudentsByID(List<Integer> IDList) throws NoSuchElementException {
        if(IDList.size() != movableStudents) { return null; }
        List<Student> students = new ArrayList<>();
        for(int ID : IDList){
            Student s = removePawnByID(ID);
            students.add(s);
        }
        return students;
    }

    public Student removeStudentByID(int studentID) {
        return removePawnByID(studentID);
    }


}

