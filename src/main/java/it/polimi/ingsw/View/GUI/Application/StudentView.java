package it.polimi.ingsw.View.GUI.Application;


import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class StudentView extends PawnView {

    public static double studentSize = 15.0;

    private String color;

    public StudentView(int id, String pawnType, String color, double size) {
        super(id, pawnType, color, size);
        this.color = color;
    }

    public void setDisabled() {
        this.setMouseTransparent(true);
        this.setEffect(new DropShadow(studentSize/2, Color.DIMGREY));
        this.setOnMouseEntered(mouseEvent -> {});
        this.setOnMouseExited(mouseEvent -> {});
    }

    public void setEnabled() {
        this.setMouseTransparent(false);
        this.setEffect(new DropShadow(studentSize, Color.LIGHTCYAN));
        this.setOnMouseEntered(mouseEvent -> this.setEffect(new DropShadow(studentSize*2, Color.BLUE)));
        this.setOnMouseExited(mouseEvent -> this.setEffect(new DropShadow(studentSize, Color.LIGHTCYAN)));
    }

    public void setCallback(EventHandler<MouseEvent> eventHandler) {
        this.setOnMouseClicked(eventHandler);
    }

    public String getColor() {
        return color;
    }
}
