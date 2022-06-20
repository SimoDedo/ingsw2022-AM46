package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Class that models a generic pawn to be placed in various panes, this includes towers, mother nature, no entry tiles,
 * professors and students.
 * The pawn is created according to the parameters given.
 */
public class PawnView extends ImageView {

    /**
     * The standard size of the pawn
     */
    public static final double pawnSize = 25.0;

    /**
     * The constructor of the PawnView
     * @param id The ID of pawn to create
     * @param pawnType The type of the pawn to create. The possible types are "tower", "mothernature", "noentry" and
     *                 "professor" or "student"
     * @param color The color of the pawn to create. If the pawn doesn't need a color, a blank string is required.
     *              The possible colors are "white", "black" and "grey" for a tower and "red", "blue", "green",
     *              "yellow" and "pink" for a professor or a student.
     * @param size The size of the pawn
     */
    public PawnView(int id, String pawnType, String color, double size) {
        this.setId(pawnType + id);
        Image image = new Image("/pawns/" + pawnType + (color.equals("") ? color : "_" + color) + ".png");
        this.setImage(image);
        this.setEffect(new DropShadow(size/2, Color.BLACK));
        this.setPreserveRatio(true);
        this.setFitHeight(size);
        this.setSmooth(true);
        this.setCache(true);
        this.setPickOnBounds(false);

        this.setMouseTransparent(true);
    }

}
