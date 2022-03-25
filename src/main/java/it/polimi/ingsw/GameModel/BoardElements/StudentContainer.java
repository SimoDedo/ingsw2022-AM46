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
     * Removes student from previous container, changes its container to this one and places it inside
     * @param student student to be placed inside this container
     */
    @Override //FIXME: why merge placePawn and removePawn in a single function?
    public void placePawn(Student student) {
        Student temp = student.getStudentContainer().removePawn(student); //TODO: should throw an exception if temp is null
        super.placePawn(student);
        student.setStudentContainer(this);
    }

}
