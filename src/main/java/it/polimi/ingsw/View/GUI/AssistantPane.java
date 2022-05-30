package it.polimi.ingsw.View.GUI;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class AssistantPane extends AnchorPane {

    private final double sizeAssistant;

    private final GridPane assistantGrid;


    public AssistantPane(int position, double sizeAssistant) {
        this.sizeAssistant = sizeAssistant;
        assistantGrid = new GridPane();
        this.setId("assistantPane" + position);
        for (int i = 1; i < 11; i++) {
                setAssistant(position, i);
        }
        this.getChildren().add(assistantGrid);
    }

    private void setAssistant(int position, int ID){
        Image assistant = new Image("/deck/animali_1_" + ID + "@3x.png", 200, 200, true, true);
        ImageView imageViewAssistant = new ImageView(assistant);
        imageViewAssistant.setPreserveRatio(true);
        imageViewAssistant.setFitHeight(sizeAssistant);
        imageViewAssistant.setId("assistant" + position + ID);
        setZoomOnAssistant(imageViewAssistant);
        assistantGrid.add(imageViewAssistant, (ID > 5 ? ID - 5 : ID), (ID > 5 ? 1 : 0));
    }

    public ImageView removeAssistant(int position, int ID){
        String idToFind = "assistant" + position + ID;
        ImageView imageView = null;
        for (Node assistant : assistantGrid.getChildren()){
            if(assistant.getId() != null && assistant.getId().equals(idToFind)){
                imageView = (ImageView) assistant;
                assistantGrid.getChildren().remove(assistant);
                break;
            }
        }
        return imageView;
    }

    private void setZoomOnAssistant(ImageView imageView){
        imageView.onMouseEnteredProperty().set(event -> {
            imageView.setEffect(new DropShadow(10.0, Color.SEAGREEN));
            ImageView toZoom = new ImageView(imageView.getImage());
            toZoom.setId("toZoomAssistant");
            toZoom.setPreserveRatio(true);
            toZoom.setFitHeight(sizeAssistant * 2);
            toZoom.setEffect(new DropShadow());
            toZoom.setMouseTransparent(true);
            this.getChildren().add(toZoom);

            if(event.getSceneX() > assistantGrid.localToScene(getBoundsInLocal()).getCenterX()){
                AnchorPane.setLeftAnchor(toZoom, 0.0);
            }
            else{
                AnchorPane.setRightAnchor(toZoom, 0.0);
            }
        });
        imageView.onMouseExitedProperty().set(event -> {
            imageView.setEffect(null);
            for(Node node : this.getChildren()){
                if(node.getId() != null && node.getId().equals("toZoomAssistant")){
                    this.getChildren().remove(node);
                    break;
                }
            }
            this.getChildren().get(0).getId();
        });
    }
}
