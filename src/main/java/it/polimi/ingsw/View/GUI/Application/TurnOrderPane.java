package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Phase;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Pane that displayed the current turn, player and player order.
 */
public class TurnOrderPane extends HBox {


    /**
     * Constructor for a turn order pane with dummy values.
     */
    public TurnOrderPane() {
        super(15.0);
        this.setId("turnOrderPane");
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(new Label("Player 1"));
        this.getChildren().add(new Label("Player 2"));
        this.getChildren().add(new Label("Player 3"));
        this.getChildren().add(new Label("Player 4"));
    }

    /**
     * Updates the turn order pane according to the parameters given.
     * @param currentPhase The phase currently being played.
     * @param currentPlayer The player currently playing.
     * @param order The current player order.
     */
    public void updateTurnOrderPane(Phase currentPhase, String currentPlayer, List<String> order){
        this.getChildren().clear();
        String phaseFormatted = currentPhase.toString().charAt(0) + currentPhase.toString().substring(1).toLowerCase();
        Label phase = new Label(phaseFormatted + " phase    | ");
        phase.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 15));
        this.getChildren().add(phase);

        for(String nick : order){
            Label toPut = new Label(nick);
            toPut.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 12));
            if(nick.equals(currentPlayer)){
                toPut.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_BOLD, 15));
                if (phaseFormatted.equals("Planning")) {
                    toPut.setTextFill(Color.DARKGREEN);
                } else {
                    toPut.setTextFill(Color.DARKRED);
                }
            }
            else{
                if(nick.length() > 10)
                    toPut.setText(nick.substring(0,9) + "...");
            }
            this.getChildren().add(toPut);
        }
    }
}
