package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.ObservableGUI;
import it.polimi.ingsw.View.GUI.ObserverGUI;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains the cloud panes of the game.
 */
public class CloudContainerPane extends HBox implements ObservableGUI {

    /**
     * The observer of this GUI element.
     */
    private ObserverGUI observer;

    /**
     * The height of the cloud container. It is equal to the height of a cloud.
     */
    static final double cloudContainerHeight = CloudPane.cloudSize;

    /**
     * The width of the cloud container. It is greater than the width of four clouds.
     */
    static final double cloudContainerWidth = CloudPane.cloudSize*4.5;

    /**
     * The ID of the chosen cloud.
     */
    private int cloudChosen;

    /**
     * The ID of the clouds in this container.
     */
    private List<Integer> cloudsIDs;

    /**
     * Constructor for the class. Sets the container's ID, alignment and size.
     */
    public CloudContainerPane() {
        super(5.0);
        this.setId("cloudContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(cloudContainerWidth, cloudContainerHeight);
        this.setMaxSize(cloudContainerWidth, cloudContainerHeight);
    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
    }

    /**
     * Sets an ID to each CloudPane based on the real ID in the server model.
     * @param numOfPlayers the number of clouds in this game
     * @param clouds the IDs of the clouds in the server model
     */
    public void createClouds(int numOfPlayers, List<Integer> clouds) {
        cloudsIDs = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            CloudPane cloudPane = new CloudPane();
            cloudPane.setId("cloudPane" + clouds.get(i));
            cloudsIDs.add(clouds.get(i));
            this.getChildren().add(cloudPane);
            cloudPane.createCloud(clouds.get(i));
        }
    }

    /**
     * Updates a cloud with the given ID with an updated list of students on it.
     * @param cloudID the ID of the cloud to update
     * @param studs a map with the student IDs and their respective color
     */
    public void updateCloud(Integer cloudID, HashMap<Integer, Color> studs){
        ((CloudPane) this.lookup("#cloudPane" + cloudID)).updateCloud(studs);
    }

    /**
     * Setter for the cloud chosen.
     * @param cloudID the ID of the chosen cloud
     */
    public void setCloudChosen(int cloudID) {
        this.cloudChosen = cloudID;
    }

    /**
     * Getter for the cloud chosen.
     * @return the ID of the chosen cloud
     */
    public int getCloudChosen() {
        return cloudChosen;
    }

    /**
     * Enables the selection of any cloud that has students in this container.
     */
    public void enableSelectCloud() {
        for (Integer cloudID : cloudsIDs) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + cloudID);
            StudentContainerPane studentContainerPane = (StudentContainerPane) cloudPane.lookup("#cloudStudentsPane" + cloudID);
            if(! studentContainerPane.getChildren().isEmpty()){
                cloudPane.setEffect(Effects.enabledCloudEffect);
                cloudPane.setOnMouseEntered(e -> cloudPane.setEffect(Effects.hoveringCloudEffect));
                cloudPane.setOnMouseExited(e -> cloudPane.setEffect(Effects.enabledCloudEffect));
                int finalCloudID = cloudID;
                cloudPane.setOnMouseClicked(event -> {
                    setCloudChosen(finalCloudID);
                    observer.notifyCloud();
                });
            }
        }
    }

    /**
     * Disables the selection of any cloud in this container.
     */
    public void disableSelectCloud() {
        for (Integer cloudID : cloudsIDs) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + cloudID);
            cloudPane.setEffect(Effects.disabledCloudEffect);
            cloudPane.setOnMouseEntered(e ->{});
            cloudPane.setOnMouseExited(e ->{});
            cloudPane.setOnMouseClicked(event -> {
            });
        }
    }

}
