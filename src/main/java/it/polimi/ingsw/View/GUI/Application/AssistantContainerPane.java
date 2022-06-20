package it.polimi.ingsw.View.GUI.Application;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the assistant cards of a user.
 */
public class AssistantContainerPane extends AnchorPane {

    /**
     * An integer representing the position of the user in the players' list.
     */
    private final int nickID;

    /**
     * The height of an assistant card.
     */
    private double assistantSize;

    /**
     * The gap in pixels between assistant cards.
     */
    private double assistantGridGap = 2.0;

    /**
     * The GridPane containing the assistant cards.
     */
    private final GridPane assistantGrid;

    /**
     * The assistant card currently chosen.
     */
    private int assistantChosen = -1;

    /**
     * A list containing the IDs of the assistants still available.
     */
    private final List<Integer> assistantsLeft;

    /**
     * Constructor for the class. It initializes the assistant cards and the grid containing them, with the appropriate size.
     * @param nickID the index of the PlayerPane that has this AssistantPane
     * @param assistantSize the height of the assistant cards
     */
    public AssistantContainerPane(int nickID, double assistantSize) {
        this.nickID = nickID;
        this.assistantSize = assistantSize;
        assistantGrid = new GridPane();
        assistantGrid.setHgap(assistantGridGap);
        assistantGrid.setVgap(assistantGridGap);
        this.setId("assistantContainerPane" + nickID);

        assistantsLeft = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
                setAssistant(i);
                assistantsLeft.add(i);
        }
        this.getChildren().add(assistantGrid);
    }

    /**
     * Sets the assistant card with the given ID: creates an ImageView for the card, sets its ID, its effects.
     * @param ID the ID of the assistant to create
     */
    private void setAssistant(int ID) {
        Image assistant = new Image("/deck/assistant" + ID + ".png", 200, 200, true, true);
        ImageView imageViewAssistant = new ImageView(assistant);
        imageViewAssistant.setPreserveRatio(true);
        imageViewAssistant.setFitHeight(assistantSize);
        imageViewAssistant.setId("assistant" + nickID + ID);
        imageViewAssistant.setEffect(Effects.disabledAssistantEffect);
        setZoomOnAssistant(imageViewAssistant, Effects.disabledStudentEffect, Effects.disabledAssistantEffect);
        assistantGrid.add(imageViewAssistant, (ID > 5 ? ID - 5 : ID), (ID > 5 ? 1 : 0));
    }

    /**
     * Method that resizes an assistant card by a given factor.
     * @param resizeFactor a double (must be between 0 and 1)
     */
    public void resizeAssistant(double resizeFactor){
        assistantSize = assistantSize * resizeFactor;
        for(Node assistantView : assistantGrid.getChildren()){
            ((ImageView) assistantView).setFitHeight(assistantSize);
        }
        assistantGridGap = assistantGridGap * resizeFactor;
        assistantGrid.setVgap(assistantGridGap);
        assistantGrid.setHgap(assistantGridGap);
    }

    /**
     * Setter for the chosen assistant.
     * @param assistantID the ID of the assistant chosen
     */
    public void setAssistantChosen(int assistantID) {
        this.assistantChosen = assistantID;
    }

    /**
     * Getter for the chosen assistant.
     * @return the ID of the assistant chosen
     */
    public int getAssistantChosen() {
        return assistantChosen;
    }

    /**
     * Updates the AssistantPane with the new list of available assistants.
     * @param newAssistantsLeft a list containing the available assistants left
     */
    public void updateAssistant(List<Integer> newAssistantsLeft){
        for(Integer assistant : assistantsLeft){
            if(! newAssistantsLeft.contains(assistant)){
                this.lookup("#assistant" + nickID + assistant).setVisible(false);
            }
        }
    }

    /**
     * Method that allows to zoom in on an assistant, displaying an enlarged version of the card to take a better look.
     * @param imageView the ImageView on which to set the zoom effect prompt
     * @param onEnter the effect to display when the mouse enters the area
     * @param onExit the effect to display when the mouse leaves the area
     */
    public void setZoomOnAssistant(ImageView imageView, Effect onEnter, Effect onExit){
        imageView.onMouseEnteredProperty().set(event -> {
            imageView.setEffect(onEnter);
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
            imageView.setEffect(onExit);
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
