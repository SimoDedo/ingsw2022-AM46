package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class TurnOrderPane extends HBox {

    public TurnOrderPane() {
        super(10.0);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(new Label("Player 1"));
        this.getChildren().add(new Label("Player 2"));
        this.getChildren().add(new Label("Player 3"));
        this.getChildren().add(new Label("Player 4"));
    }
}
