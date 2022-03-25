package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class which extends PawnContainer using Students specifically
 */
public abstract class StudentContainer extends PawnContainer<Student>{

    /**
     * Creates StudentContainer with owner and maxPawns
     *
     * @param player
     * @param maxPawns
     */
    public StudentContainer(Player player, int maxPawns) {
        super(player, maxPawns);
    }

    /**
     * When placing a students also removes it from whichever StudentContainer it was before, then sets itself as the Student's StudentContainer
     * @param student
     */
    @Override
    public void PlacePawn(Student student) {
        Student temp = student.getStudentContainer().RemovePawn(student); //TODO: should check temp to handle some esceptions?
        super.PlacePawn(student);
        student.setStudentContainer(this);
    }

}
