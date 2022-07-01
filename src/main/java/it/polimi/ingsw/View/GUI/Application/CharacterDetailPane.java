package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.CharactersDescription;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains a zoomed-in version of the characters with their ability description. It can be called at any point
 * during the game by pressing H (pressing H again will hide it).
 */
public class CharacterDetailPane extends HBox {

    /**
     * The width of each character card.
     */
    private final double size = 250.0;

    /**
     * Whether the detail pane is active or not.
     */
    private boolean active;

    /**
     * Constructor for the class. Sets the pane to invisible and inactive.
     */
    public CharacterDetailPane() {
        super(10.0);
        this.setVisible(false);
        active = false;
    }

    /**
     * Getter for the active boolean.
     * @return true if the pane is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active boolean
     * @param active the new active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Method for adding characters to the pane in bulk.
     * @param charIDs a list of character IDs to add to the pane
     */
    public void addCharacters(List<Integer> charIDs){
        for(Integer ID : charIDs){
            addCharacter(ID);
        }
    }

    /**
     * Adds a character's image and its ability description on top, and inserts it into this pane.
     * @param ID the ID of the character to display
     */
    private void addCharacter(int ID){
        StackPane charDesc = new StackPane();

        Image image = new Image("/chars/char"+ID+".png");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        DropShadow effect = new DropShadow(size/10, Color.BLACK);
        effect.setInput(new ColorAdjust(0.0, -0.15, -0.00,-0.15));
        imageView.setEffect(effect);
        imageView.setFitWidth(size);

        Text desc = new Text(Arrays.stream(CharactersDescription.values()).toList().get(ID - 1).getDescription());
        desc.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 24.0));
        desc.setFill(Color.WHITE);
        desc.setStroke(Color.BLACK);
        desc.setStrokeWidth(1.0);
        desc.setEffect(new DropShadow(60.0, Color.DIMGREY));
        desc.setMouseTransparent(true);
        desc.setVisible(true);
        desc.setWrappingWidth(size - size/20.0);

        charDesc.getChildren().add(imageView);
        charDesc.getChildren().add(desc);

        this.getChildren().add(charDesc);
    }
}
