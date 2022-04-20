package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.BoardElements.PlaceAndRemoveStudent;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * This subclass of AbstractCharacter is a sort of StudentContainer. It has a list of students it can
 * hold, place/remove methods inherited from the PlaceAndRemoveStudent interface, and a number of uses
 * (i.e. how many students a player can move per character activation).
 */
public class StudentMoverCharacter extends AbstractCharacter implements PlaceAndRemoveStudent {

    private int usesLeft, maxUses, maxPawns;

    private List<Student> students;

    public StudentMoverCharacter(int ID, int cost, int maxUses, int maxPawns, List<RequestParameter> requestParameters) {
        super(ID, cost, requestParameters);
        this.maxUses = maxUses;
        this.maxPawns = maxPawns;
        this.usesLeft = maxUses;
        students = new ArrayList<>();
    }

    /**
     * Uses the character ability and decreases the number of uses left for this character.
     * @param consumer the Consumer that acts on the GameModel
     * @param parameterList the list of the consumer's parameters
     * @throws IllegalStateException if the character has no uses left
     */
    @Override
    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) throws IllegalStateException, LastRoundException, GameOverException {
        if (usesLeft > 0) {
            consumer.accept(parameterList);
            usesLeft--;
        } else throw new IllegalStateException("Character uses depleted");
    }

    /**
     * Resets the owner, wasUsedThisTurn attribute and sets the number of uses back to normal.
     */
    @Override
    public void resetUseState() {
        super.resetUseState();
        usesLeft = maxUses;
    }

    /**
     * Places Student on the character.
     * @param student student to be placed inside this container
     */
    @Override
    public void placePawn(Student student) {
        if(students.size() == maxPawns) throw new IllegalArgumentException("The container is full");
        if (students.contains(student)) throw new IllegalArgumentException("The student is already on this card");
        students.add(student);
        student.setStudentContainer(this);
    }

    /**
     * Removes a Student from the Character.
     * @param studentToRemove Student to be removed
     * @return the Student removed (the same given)
     */
    @Override
    public boolean removePawn(Student studentToRemove) {
        studentToRemove.setStudentContainer(null);
        return students.remove(studentToRemove);
    }

    @Override
    public Student removePawnByID(int ID) {
        Student studentToRemove = students.stream()
                .filter(student -> student.getID() == ID)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No such element in container"));
        removePawn(studentToRemove);
        return studentToRemove;
    }

    /**
     * Getter for the Student with the given ID
     * @param ID the ID of the Student to find
     * @return the Student with that ID
     */
    @Override
    public Student getPawnByID(int ID) {
        return students.stream()
                .filter(student -> student.getID() == ID)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No such element in container"));
    }

    /**
     * Getter for the number of uses left.
     * @return the number of uses left
     */
    public Object getUsesLeft() {
        return usesLeft;
    }

    /**
     * Getter for the number of students placed on this character.
     * @return the number of students placed on this character
     */
    public int getSize() {
        return students.size();
    }

    /**
     * Getter for a list of IDs of pawns contained in the PawnContainer
     * @return A new list of Integers containing the IDs
     */
    public List<Integer> getPawnIDs() {
        List<Integer> IDs = new ArrayList<>();
        for(Student student : students){
            IDs.add(student.getID());
        }
        return IDs;
    }

    /**
     * Method to observe all the students in the entrance and their color
     * @return HashMap with the student ID as key and its color as object
     */
    public HashMap<Integer, Color> getStudentIDsAndColor(){
        HashMap<Integer, Color> result = new HashMap<>();
        for(Student student : students){
            result.put(student.getID(), student.getColor());
        }
        return result;
    }
}
