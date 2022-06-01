package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.List;

public class CloudContainerPane extends HBox {

    private GUIController controller;

    static double cloudContainerHeight = CloudPane.cloudSize, cloudContainerWidth = CloudPane.cloudSize*4.5;

    private int cloudNumber;

    private int cloudChosen;

    public CloudContainerPane(GUIController controller) {
        super(5.0);
        this.controller = controller;
        this.setId("cloudContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(cloudContainerWidth, cloudContainerHeight);
        this.setMaxSize(cloudContainerWidth, cloudContainerHeight);
    }

    public void setCloudChosen(int cloudID) {
        this.cloudChosen = cloudID;
    }

    public int getCloudChosen() {
        return cloudChosen;
    }

    public void emptyCloud(int cloudChosen) {
    }

    public void createClouds(int numOfPlayers, List<Integer> cloudIDs) {
        this.cloudNumber = numOfPlayers;
        for (int i = 0; i < numOfPlayers; i++) {
            CloudPane cloudPane = new CloudPane();
            cloudPane.setId("cloudPane" + i);
            this.getChildren().add(cloudPane);
            cloudPane.createCloud(cloudIDs.get(i));
        }
    }

    public void enableSelectCloud() {
        for (int i = 0; i < cloudNumber; i++) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + i);
            int cloudIndex = i;
            cloudPane.setOnMouseClicked(event -> {
                System.out.println("Someone clicked on me!" + cloudPane.getId());
                setCloudChosen(cloudIndex);
                controller.notifyCloud();
            });
        }
    }

    public void disableSelectCloud() {
        for (int i = 0; i < cloudNumber; i++) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + i);
            cloudPane.setOnMouseClicked(event -> {
                System.out.println("I'm disabled");
            });
        }
    }
}
