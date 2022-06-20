package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * This class serves as a pane with which to select a color out of the available color
 */
public class ColorSelectionPane extends VBox {

    /**
     * The size of the color button.
     */
    public static final double colorSize = 17.0;

    /**
     * The order in which colors are displayed, from top to bottom.
     */
    public static Map<Color, Integer> colorOrder;

    /**
     * Constructor for the class. It creates the color buttons and sets itself to invisible.
     * @param charID the ID of the character that owns this color selection pane
     */
    public ColorSelectionPane(int charID) {
        this.setId("char" + charID + "ColorPane");
        this.setVisible(false);
        this.setSpacing(2.5);
        colorOrder = new HashMap<>();

        int i = 0;
        for (Color color : Color.values()) {
            colorOrder.put(color, i++);
            ImageView colorView = new ImageView();
            colorView.setId("char" + charID + "Color" + color.toString().toLowerCase());
            Image colorImage = new Image("/chars/color_" + color.toString().toLowerCase() + ".png");
            colorView.setImage(colorImage);
            colorView.setEffect(Effects.disabledStudentEffect);
            colorView.setPreserveRatio(true);
            colorView.setFitHeight(colorSize);
            colorView.setSmooth(true);
            colorView.setCache(true);
            colorView.setPickOnBounds(false);
            this.getChildren().add(colorView);
        }
    }
}
