package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.ObservableGUI;
import it.polimi.ingsw.View.GUI.ObserverGUI;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CloudContainerPane extends HBox implements ObservableGUI {

    private ObserverGUI observer;

    static double cloudContainerHeight = CloudPane.cloudSize, cloudContainerWidth = CloudPane.cloudSize*4.5;

    private int cloudChosen;

    private List<Integer> cloudsIDs;

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

    public void updateCloud(Integer cloud, HashMap<Integer, Color> studs){
        ((CloudPane) this.lookup("#cloudPane" + cloud)).updateCloud(studs);
    }

    public void setCloudChosen(int cloudID) {
        this.cloudChosen = cloudID;
    }

    public int getCloudChosen() {
        return cloudChosen;
    }

    public void enableSelectCloud() {
        for (Integer cloudID : cloudsIDs) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + cloudID);
            cloudPane.setEffect(Effects.enabledCloudShadow);
            cloudPane.setOnMouseEntered(e -> cloudPane.setEffect(Effects.hoveringCloudShadow));
            cloudPane.setOnMouseExited(e -> cloudPane.setEffect(Effects.enabledCloudShadow));
            int finalCloudID = cloudID;
            cloudPane.setOnMouseClicked(event -> {
                setCloudChosen(finalCloudID);
                observer.notifyCloud();
            });
        }
    }

    public void disableSelectCloud() {
        for (Integer cloudID : cloudsIDs) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + cloudID);
            cloudPane.setEffect(Effects.disabledCloudShadow);
            cloudPane.setOnMouseEntered(e ->{});
            cloudPane.setOnMouseExited(e ->{});
            cloudPane.setOnMouseClicked(event -> {
            });
        }
    }

    public void debugStud() {
        for (Integer cloudID : cloudsIDs) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + cloudID);
            if (cloudPane != null)
                cloudPane.debugStud();
        }
    }
}
