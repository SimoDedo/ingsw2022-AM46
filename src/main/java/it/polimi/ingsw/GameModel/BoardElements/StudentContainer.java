package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class which extends PawnContainer, using Students specifically
 */
public abstract class StudentContainer extends PawnContainer<Student>{

    /**
     * Creates StudentContainer with owner and maxPawns
     *
     * @param player the player who owns the container
     * @param maxPawns the max number of pawns the container can hold
     */
    public StudentContainer(Player player, int maxPawns) {
        super(player, maxPawns);
    }

    /**
     * Places student inside the container, then sets itself as its container
     * @param student student to be placed inside this container
     */
    @Override
    public void placePawn(Student student) {
        super.placePawn(student);
        student.setStudentContainer(this);
    }


    /**
     * Sets the StudentContainer to null, then removes the student from itself
     * @param pawnToRemove Student to be removed
     * @return The Student removed (the same given)
     */
    @Override
    public Student removePawn(Student pawnToRemove) {
        pawnToRemove.setStudentContainer(null);
        return super.removePawn(pawnToRemove);
    }

    /**
     *  Alternativa to RemovePawn using index
     * @param index index of the pawn to be removed
     * @return The student removed
     * @throws IllegalStateException When no student with such ID is contained
     */
    @Override
    public Student removePawnByIndex(int index) throws IllegalStateException{
        Student student = super.removePawnByIndex(index);
        student.setStudentContainer(null);
        return student;
    }

    /**
     * Removes the student from its former container (if it has one, but it should) then places it in this container.
     * @param studentToPlace Student to place in this container
     * @return The student removed from the StudentContainer of the student (same as the studentToPlace)
     */
    public Student moveStudent(Student studentToPlace) { // deprecated
        Student studentToReturn = null;
        if(studentToPlace.getStudentContainer() != null)
            studentToReturn = studentToPlace.getStudentContainer().removePawn(studentToPlace);
        placePawn(studentToPlace);
        return studentToReturn;
    }
}
