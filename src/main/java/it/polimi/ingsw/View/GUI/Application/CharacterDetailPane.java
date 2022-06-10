package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.CharactersDescription;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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


public class CharacterDetailPane extends HBox {

    private final double size = 250.0;

    private boolean active;

    public CharacterDetailPane() {
        super(10.0);
        this.setVisible(false);
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addCharacters(List<Integer> charIDs){
        for(Integer ID : charIDs){
            addCharacter(ID);
        }
    }

    private void addCharacter(int ID){
        StackPane charDesc = new StackPane();

        Image image = new Image("/chars/char"+ID+".png");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setEffect(new DropShadow(size/10, Color.BLACK));
        imageView.setFitWidth(size);

        Text desc = new Text(Arrays.stream(CharactersDescription.values()).toList().get(ID - 1).getDescription());
        desc.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 20));
        desc.setFill(Color.WHITE);
        desc.setStroke(Color.BLACK);
        desc.setStrokeWidth(0.8);
        desc.setEffect(new DropShadow(15.0, Color.WHITE));
        desc.setMouseTransparent(true);
        desc.setVisible(true);
        desc.setWrappingWidth(size - size/20);

        charDesc.getChildren().add(imageView);
        charDesc.getChildren().add(desc);

        this.getChildren().add(charDesc);
    }
}
