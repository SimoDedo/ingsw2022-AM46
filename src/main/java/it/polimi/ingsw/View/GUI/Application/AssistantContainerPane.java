package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class AssistantContainerPane extends AnchorPane {

    private final String nickname;

    private double assistantSize;

    private double assistantGridGap = 2.0;

    private final GridPane assistantGrid;

    private int assistantChosen = -1;

    private final List<Integer> assistantsLeft;


    public AssistantContainerPane(String nickname, double assistantSize) {
        this.nickname = nickname;
        this.assistantSize = assistantSize;
        assistantGrid = new GridPane();
        assistantGrid.setHgap(assistantGridGap);
        assistantGrid.setVgap(assistantGridGap);
        this.setId("assistantContainerPane" + nickname);

        assistantsLeft = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
                setAssistant(i);
                assistantsLeft.add(i);
        }
        this.getChildren().add(assistantGrid);
    }

    private void setAssistant(int ID){
        Image assistant = new Image("/deck/assistant" + ID + ".png", 200, 200, true, true);
        ImageView imageViewAssistant = new ImageView(assistant);
        imageViewAssistant.setPreserveRatio(true);
        imageViewAssistant.setFitHeight(assistantSize);
        imageViewAssistant.setId("assistant" + nickname + ID);
        setZoomOnAssistant(imageViewAssistant);
        assistantGrid.add(imageViewAssistant, (ID > 5 ? ID - 5 : ID), (ID > 5 ? 1 : 0));
    }

    public void resizeAssistant(double resizeFactor){
        assistantSize = assistantSize * resizeFactor;
        for(Node assistantView : assistantGrid.getChildren()){
            ((ImageView) assistantView).setFitHeight(assistantSize);
        }
        assistantGridGap = assistantGridGap * resizeFactor;
        assistantGrid.setVgap(assistantGridGap);
        assistantGrid.setHgap(assistantGridGap);
    }

    public void setAssistantChosen(int assistantID) {
        this.assistantChosen = assistantID;
    }

    public int getAssistantChosen() {
        return assistantChosen;
    }

    public void updateAssistant(List<Integer> newAssistantsLeft){
        for(Integer assistant : assistantsLeft){
            if(! newAssistantsLeft.contains(assistant)){
                this.lookup("#assistant" + nickname + assistant).setVisible(false);
            }
        }
    }

    private void setZoomOnAssistant(ImageView imageView){
        imageView.onMouseEnteredProperty().set(event -> {
            imageView.setEffect(new DropShadow(10.0, Color.SEAGREEN));
            ImageView toZoom = new ImageView(imageView.getImage());
            toZoom.setId("toZoomAssistant");
            toZoom.setPreserveRatio(true);
            toZoom.setFitHeight(assistantSize * 2);
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
