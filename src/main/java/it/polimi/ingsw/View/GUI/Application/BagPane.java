package it.polimi.ingsw.View.GUI.Application;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * This class is for creating the two bags on the board,  the students bag and the coin bag (also called coin heap).
 */
public class BagPane extends StackPane {

    /**
     * The type of this bag: normally either "students" or "coins".
     */
    private final String bagType;

    /**
     * The height and width of the bag.
     */
    public static double bagSize = 60.0;

    /**
     * Constructor for the class. Sets the right size, applies effects and creates the counter on top of the image.
     * @param bagType the type of this bag: normally either "students" or "coins"
     */
    public BagPane(String bagType) {
        this.bagType = bagType;
        this.setId(bagType + "BagPane");
        this.setEffect(new DropShadow());
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(bagSize, bagSize);

        Image bag = new Image("/world/bag_" + bagType + ".png", 100.0, 100.0, true, true);
        ImageView bagView = new ImageView(bag);
        bagView.setPreserveRatio(true);
        bagView.setFitHeight(bagSize);
        bagView.setSmooth(true);
        bagView.setCache(true);
        this.getChildren().add(bagView);

        Text numRemainingText = new Text(String.valueOf(0));
        numRemainingText.setId(bagType + "BagPaneCounter");
        numRemainingText.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, bagSize * 0.4));
        numRemainingText.setFill(Color.WHITE);
        numRemainingText.setEffect(new DropShadow(20, Color.BLACK));
        this.getChildren().add(numRemainingText);
    }

    /**
     * Method for updating the counter on this bag.
     * @param count the updated count
     */
    public void updateCount(int count){
        ((Text)this.lookup("#" + bagType +"BagPaneCounter")).setText(String.valueOf(count));
    }
}
