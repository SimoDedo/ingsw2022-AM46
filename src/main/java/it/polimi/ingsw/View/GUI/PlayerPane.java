package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class PlayerPane extends StackPane {

    public PlayerPane(Pos position) {
        // based on the position, there are different orders and orientations for the elements in
        // the player's side of the table: deck/board first? horizontal/vertical cards and board?
        // etc. The idea is to parameterize the four different orientations of the structure, leaving
        // identical underlying core mechanics so one can easily modify all four player boards at
        // once.

    }
}
