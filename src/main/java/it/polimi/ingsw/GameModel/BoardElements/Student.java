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
    private PlaceAndRemoveStudent studentContainer;

    /**
     * Creates a Student contained in a given StudentContainer, with a unique ID and a given color
     * @param studentContainer the initial container of this student
     */
    public Student(Color color, PlaceAndRemoveStudent studentContainer){
        super();
        this.color = color;
        this.studentContainer = studentContainer;
    }

    /**
     * Getter for Color
     * @return the color of this student
     */
    public Color getColor() {
        return color;
    }

    /**
     * Getter for StudentContainer
     * @return the current container of this student
     */
    public PlaceAndRemoveStudent getStudentContainer() {
        return studentContainer;
    }

    /**
     * Setter for StudentContainer
     * @param studentContainer the future container of this student
     */
    public void setStudentContainer(PlaceAndRemoveStudent studentContainer) {
        this.studentContainer = studentContainer;
    }

}
