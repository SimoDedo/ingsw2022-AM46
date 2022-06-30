package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class represents the cloud inside the cloud container.
 */
public class CloudPane extends StackPane {

    /**
     * The height and width of the cloud.
     */
    public static final double cloudSize = 100;

    /**
     * The StudentContainerPane on this cloud.
     */
    private StudentContainerPane studentPane;

    /**
     * A list containing the "coordinates" (column and row) of the free spots in the cloud's student pane.
     */
    private List<Pair<Integer, Integer>> freeStudSpots;

    /**
     * Constructor for the class. Sets ID, alignment, size and image.
     */
    public CloudPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(cloudSize, cloudSize);
        Image cloud = new Image("/clouds/cloud_card_" + ThreadLocalRandom.current().nextInt(0, 5) + ".png",
                200, 200, true, true);
        ImageView cloudView = new ImageView(cloud);
        cloudView.setPreserveRatio(true);
        cloudView.setFitHeight(cloudSize*0.8);
        cloudView.setSmooth(true);
        cloudView.setCache(true);
        this.getChildren().add(cloudView);
        this.setEffect(Effects.disabledCloudEffect);

        this.setPickOnBounds(false);
    }

    /**
     * Creates the cloud with the given ID. Sets size, alignment and student space.
     * @param ID the ID of the cloud to create
     */
    public void createCloud(int ID){
        studentPane = new StudentContainerPane("cloudStudentsPane", ID,
                cloudSize*0.75, cloudSize*0.75, 100, 3, 3, 20.0, 23.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        this.getChildren().add(studentPane);
        freeStudSpots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                freeStudSpots.add(new Pair<>(i, j));
            }
        }
    }

    /**
     * Updates the cloud with a map of the students present on it.
     * @param newStuds a map with the ID of the students present on the cloud and their respective color
     */
    public void updateCloud(HashMap<Integer, Color> newStuds){
        if(newStuds.isEmpty() && ! studentPane.getChildren().isEmpty()){
            studentPane.getChildren().clear();
            freeStudSpots.clear();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    freeStudSpots.add(new Pair<>(i, j));
                }
            }
        }
        else if(! newStuds.isEmpty() && studentPane.getChildren().isEmpty()){
            for(Integer stud : newStuds.keySet()){
                int rand = ThreadLocalRandom.current().nextInt(0, freeStudSpots.size());
                studentPane.add(new StudentView(stud, newStuds.get(stud).toString().toLowerCase(), StudentView.studentSize),
                        freeStudSpots.get(rand).getKey(), freeStudSpots.get(rand).getValue());
                freeStudSpots.remove(rand);
            }
        }
    }

}
