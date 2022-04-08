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
    public void placePawn(Student student) throws IllegalArgumentException{
        super.placePawn(student);
        student.setStudentContainer(this);
    }


    /**
     * Sets the StudentContainer to null, then removes the student from itself
     * @param pawnToRemove Student to be removed
     * @return true if the pawn was removed.
     */
    @Override
    public boolean removePawn(Student pawnToRemove) {
        pawnToRemove.setStudentContainer(null);
        return super.removePawn(pawnToRemove);
    }

    /**
     * @param ID the ID of the student to remove
     * @return the student if it was removed, null otherwise.
     */
    @Override
    public Student removePawnByID(int ID){
        Student s = getPawnByID(ID);
        if(removePawn(s)) return s;
        return null;
    }

    @Override
    public Student removePawn(){
        Student s = super.removePawn();
        s.setStudentContainer(null);
        return s;
    }
    /**
     * Removes the student from its former container (if it has one, but it should) then places it in this container.
     * @param studentToPlace Student to place in this container
     */
    public void moveStudent(Student studentToPlace) throws IllegalArgumentException{
        if(studentToPlace.getStudentContainer() != null) {
            studentToPlace.getStudentContainer().removePawn(studentToPlace);}

        placePawn(studentToPlace);
    }

}
