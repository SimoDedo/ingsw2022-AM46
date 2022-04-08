package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Entrance extends StudentContainer{

    private final int movableStudents;
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
    public void fillInitial() {
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
    public List<Student> removeStudentsByID(List<Integer> IDList) throws NoSuchElementException, IllegalArgumentException {
        if(IDList.size() != movableStudents) { throw new IllegalArgumentException(); }
        List<Student> students = new ArrayList<>();
        for(int ID : IDList){
            Student student = getPawnByID(ID);
            students.add(student);
            removePawn(student);
        }
        return students;
    }

    public Student removeStudentByID(int studentID) {
        return removePawnByID(studentID);
    }

    /**
     * @param students drawn from the bag and moving to the entrance
     * @throws IllegalArgumentException if the size of the student list is incorrect for the game type
     */
    public void refillStudents(List<Student> students) throws IllegalArgumentException {
        if (students.size() == movableStudents) {
            for (Student s : students) { placePawn(s); }
        } else throw new IllegalArgumentException();
    }
}

