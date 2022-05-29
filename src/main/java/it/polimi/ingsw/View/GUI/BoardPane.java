package it.polimi.ingsw.View.GUI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class BoardPane extends StackPane {

    private final int sizeBoard = 170;

    public BoardPane(int position) {
        this.setId("playerPane" + position);
        Image playerBoard = new Image("/world/board_roundedcorners.png", 600, 600,true, false);
        ImageView imageView = new ImageView(playerBoard);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(sizeBoard);
        imageView.setSmooth(true);
        imageView.setCache(true);
        this.getChildren().add(imageView);
    }
}
