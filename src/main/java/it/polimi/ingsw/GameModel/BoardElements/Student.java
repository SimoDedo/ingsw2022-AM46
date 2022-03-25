package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;

/**
 * Models the Student piece as a BoardPiece. To avoid meaningless updates, it doesn't have an owner
 * but only a container.
 */
public class Student extends BoardPiece{
    /**
     * Color of the student. Can't change, thus it's final
     */
    private final Color color;

    /**
     * StudentContainer which hold the Student. Never null (guaranteed by PlaceStudent of studentContainer and initial constructor)
     */
    private StudentContainer studentContainer;

    /**
     * Creates a Student contained in a given StudentContainer, with an unique ID and a given color
     * @param studentContainer the initial container of this student
     */
    public Student(Color color, StudentContainer studentContainer){
        super();
        //TODO: check it isn't null? technically only bag creates the students and it always gives
        // itself as the owner, so it should never be null. Should we check anyway?
        // (Greg says no :) )
        this.color = color;
        this.studentContainer = studentContainer;
    }


    /**
     * Getter for the StudentContainer
     * @return the current container of this student
     */
    public StudentContainer getStudentContainer() {
        return studentContainer;
    }

    /**
     * Setter for the StudentContainer
     * @param studentContainer the future container of this student
     */
    public void setStudentContainer(StudentContainer studentContainer) {
        this.studentContainer = studentContainer;
    }

}
