package it.polimi.ingsw.View.GUI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class AssistantPane extends GridPane {

    private final double assistantSize = 85.0;


    public AssistantPane(int position) {
        this.setId("assistantPane" + position);
        for (int i = 1; i < 11; i++) {
            setAssistant(position, i);
        }
    }

    public void setAssistant(int position, int ID){
        Image assistant = new Image("/deck/animali_1_" + ID + "@3x.png", 200, 200, true, true);
        ImageView imageViewAssistant = new ImageView(assistant);
        imageViewAssistant.setPreserveRatio(true);
        imageViewAssistant.setFitHeight(assistantSize);
        imageViewAssistant.setId("assistant" + position + ID);
        this.add(imageViewAssistant, 8 + (ID > 5 ? ID - 5 : ID), (ID > 5 ? 1 : 0));
    }
}
