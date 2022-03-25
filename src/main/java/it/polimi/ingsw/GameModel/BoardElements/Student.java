package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;

/**
 * Models the Student piece as a BoardPiece. To avoid meaningless updates, it doesn't have a owner
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
     * Creates a Student contained in a StudentContainer with ID
     * @param studentContainer
     */
    public Student(Color color, StudentContainer studentContainer){
        super();
        //TODO: check it isn't null? technically only bag creates the students and it always gives itself, so it should never be null. Should we check anyway?
        this.color = color;
        this.studentContainer = studentContainer;
    }


    /**
     * Getter for the StudentContainer
     * @return
     */
    public StudentContainer getStudentContainer() {
        return studentContainer;
    }

    /**
     * Setter for the StudentContainer
     * @param studentContainer
     */
    public void setStudentContainer(StudentContainer studentContainer) {
        this.studentContainer = studentContainer;
    }

}
