package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerPane extends GridPane {

    private final int sizeDiscard = 100;


    public PlayerPane(int position, String nickname) {
        this.setId("playerPane" + position);

        this.setHgap(5);
        Text nickPane = new Text(nickname);
        nickPane.setId("nickPane" + position);
        nickPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 10));
        this.add(nickPane, 0 , 0);

        this.add(new BoardPane(position), 0, 1);

        Image discard = new Image("/deck/carteTOT_back_1@3x.png", 200, 200, true, true);
        ImageView imageViewDiscard = new ImageView(discard);
        imageViewDiscard.setId("discardPile" + position);
        imageViewDiscard.setPreserveRatio(true);
        imageViewDiscard.setFitHeight(sizeDiscard);
        imageViewDiscard.setEffect(new DropShadow());
        this.add(imageViewDiscard, 1, 1);

        this.add(new AssistantPane(position), 2, 1);

    }
}
