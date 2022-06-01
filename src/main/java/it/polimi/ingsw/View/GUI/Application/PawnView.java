package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PawnView extends ImageView {

    public static double pawnSize = 25.0;

    public PawnView(int id, String pawnType, String color, double size) {
        this.setId(pawnType + id);
        Image image = new Image("/pawns/" + pawnType + (color.equals("") ? color : "_" + color) + ".png");
        this.setImage(image);
        this.setEffect(new DropShadow(size/2, Color.BLACK));
        this.setPreserveRatio(true);
        this.setFitHeight(size);
        this.setSmooth(true);
        this.setCache(true);
        this.setPickOnBounds(true);

        this.setMouseTransparent(true);
    }

}
