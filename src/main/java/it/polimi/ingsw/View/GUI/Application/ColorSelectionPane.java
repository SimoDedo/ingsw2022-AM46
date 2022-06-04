package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Map;

public class ColorSelectionPane extends HBox {

    public static Map<Color, Integer> colorOrder;

    public ColorSelectionPane(int charID) {
        this.setId("char" + charID + "ColorPane");
        this.setVisible(false);

        int i = 0;
        for (Color color : Color.values()) {
            colorOrder.put(color, i++);
            ImageView colorView = new ImageView();
            colorView.setId("char" + charID + "Color" + color.toString().toLowerCase());
            Image colorImage = new Image("/chars/" + color.toString().toLowerCase() + ".png");
            colorView.setImage(colorImage);
            colorView.setEffect(new DropShadow(50.0/2.0, javafx.scene.paint.Color.BLACK));
            colorView.setPreserveRatio(true);
            colorView.setFitHeight(50.0);
            colorView.setSmooth(true);
            colorView.setCache(true);
            colorView.setPickOnBounds(false);
            this.getChildren().add(colorView);
        }
    }
}
