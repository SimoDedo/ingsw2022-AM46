package it.polimi.ingsw.View.GUI;


import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class StudentView extends PawnView {

    public static double studentSize = 15.0;

    public StudentView(int id, String pawnType, String color, double size) {
        super(id, pawnType, color, size);

    }

    public void setDisabled() {
        this.setMouseTransparent(true);
        this.setOnMouseEntered(mouseEvent -> {});
        this.setOnMouseExited(mouseEvent -> {});
        this.setEffect(new DropShadow(studentSize/2, Color.BLACK));
    }

    public void setEnabled() {
        this.setMouseTransparent(false);
        this.setEffect(new DropShadow(studentSize/2, Color.WHITE));
        this.setOnMouseEntered(mouseEvent -> this.setEffect(new DropShadow(studentSize, Color.YELLOW)));
        this.setOnMouseExited(mouseEvent -> this.setEffect(new DropShadow(studentSize/2, Color.WHITE)));
    }

    public void setCallback(EventHandler<MouseEvent> eventHandler) {
        this.setOnMouseClicked(eventHandler);
    }
}
