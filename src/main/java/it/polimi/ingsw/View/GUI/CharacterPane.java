package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CharacterPane extends StackPane {

    double charHeight = 100.0;

    public CharacterPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(charHeight, charHeight);
        Image character = new Image("/chars/char_back.png");
        ImageView charView = new ImageView(character);
        charView.setEffect(new DropShadow(50.0, Color.WHITE));
        charView.setPreserveRatio(true);
        charView.setFitHeight(charHeight);
        charView.setSmooth(true);
        charView.setCache(true);
        this.getChildren().add(charView);
    }

    public void setCharacterImage(int charID) {
        Image newChar = new Image("/chars/char" + charID + ".png", 250, 250, true, true);
        ImageView newCharView = new ImageView(newChar);
        newCharView.setEffect(new DropShadow(50.0, Color.WHITE));
        newCharView.setPreserveRatio(true);
        newCharView.setFitHeight(charHeight);
        newCharView.setSmooth(true);
        newCharView.setCache(true);
        this.getChildren().add(newCharView);
    }
}
