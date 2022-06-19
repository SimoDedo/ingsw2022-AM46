package it.polimi.ingsw.View.GUI.Application;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class StudentView extends PawnView {

    public static final double studentSize = 15.0;

    private final String color;

    public StudentView(int id, String pawnType, String color, double size) {
        super(id, pawnType, color, size);
        this.color = color;
        this.setEffect(Effects.disabledStudentShadow);
    }

    public void setDisabled() {
        this.setMouseTransparent(true);
        this.setEffect(Effects.disabledStudentShadow);
        this.setOnMouseEntered(mouseEvent -> {});
        this.setOnMouseExited(mouseEvent -> {});
    }

    public void setEnabled() {
        this.setMouseTransparent(false);
        this.setEffect(Effects.enabledStudentShadow);
        this.setOnMouseEntered(mouseEvent -> this.setEffect(Effects.hoveringStudentShadow));
        this.setOnMouseExited(mouseEvent -> this.setEffect(Effects.enabledStudentShadow));
    }

    public void setCallback(EventHandler<MouseEvent> eventHandler) {
        this.setOnMouseClicked(eventHandler);
    }

    public String getColor() {
        return color;
    }
}
