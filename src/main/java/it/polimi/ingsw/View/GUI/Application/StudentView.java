package it.polimi.ingsw.View.GUI.Application;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Class that models generic student to be placed in various panes.
 */
public class StudentView extends PawnView {

    /**
     * The standard size of a student.
     */
    public static final double studentSize = 15.0;

    /**
     * The color of this student.
     */
    private final String color;

    /**
     * Constructor for this student view.
     * @param id The ID of the student.
     * @param color The color of the student to create.
     *              The possible colors are "red", "blue", "green", "yellow" and "pink".
     * @param size The size of the student to create.
     */
    public StudentView(int id, String color, double size) {
        super(id, "student", color, size);
        this.color = color;
        this.setEffect(Effects.disabledStudentEffect);
    }

    /**
     * Disables the selection of this student.
     */
    public void setDisabled() {
        this.setMouseTransparent(true);
        this.setEffect(Effects.disabledStudentEffect);
        this.setOnMouseEntered(mouseEvent -> {});
        this.setOnMouseExited(mouseEvent -> {});
    }

    /**
     * Enables the selection of this student.
     */
    public void setEnabled() {
        this.setMouseTransparent(false);
        this.setEffect(Effects.enabledStudentEffect);
        this.setOnMouseEntered(mouseEvent -> this.setEffect(Effects.hoveringStudentEffect));
        this.setOnMouseExited(mouseEvent -> this.setEffect(Effects.enabledStudentEffect));
    }

    /**
     * Sets the function to be called when this student is clicked with the mouse.
     * @param eventHandler The callback function.
     */
    public void setCallback(EventHandler<MouseEvent> eventHandler) {
        this.setOnMouseClicked(eventHandler);
    }

    public String getColor() {
        return color;
    }
}
