package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class ColorSelectionPane extends VBox {

    public static double colorSize = 17.0;

    public static Map<Color, Integer> colorOrder;

    public ColorSelectionPane(int charID) {
        //this.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
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
            colorView.setEffect(new DropShadow(colorSize/2.0, javafx.scene.paint.Color.BLACK));
            colorView.setPreserveRatio(true);
            colorView.setFitHeight(colorSize);
            colorView.setSmooth(true);
            colorView.setCache(true);
            colorView.setPickOnBounds(false);
            this.getChildren().add(colorView);
        }
    }
}
