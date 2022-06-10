package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CloudPane extends StackPane {

    public static double cloudSize = 100.0;

    private StudentContainerPane studentPane;
    private List<Pair<Integer, Integer>> freeStudSpots;

    public CloudPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(cloudSize, cloudSize);
        Image cloud = new Image("/clouds/cloud_card_" + ThreadLocalRandom.current().nextInt(0, 5) + ".png",
                200, 200, true, true);
        ImageView cloudView = new ImageView(cloud);
        cloudView.setPreserveRatio(true);
        cloudView.setFitHeight(75.0);
        cloudView.setSmooth(true);
        cloudView.setCache(true);
        this.getChildren().add(cloudView);

        this.setPickOnBounds(false);
    }

    public void createCloud(int ID){
        studentPane = new StudentContainerPane("cloudStudentsPane", ID,
                cloudSize*0.95, cloudSize*0.95, 100, 3, 3, 25.0, 25.0, 0.0);
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
                studentPane.add(new StudentView(stud, "student", newStuds.get(stud).toString().toLowerCase(), StudentView.studentSize),
                        freeStudSpots.get(rand).getKey(), freeStudSpots.get(rand).getValue());
                freeStudSpots.remove(rand);
            }
        }
    }

    public void setEnabled() {

    }

    public void setDisabled() {

    }

    public void debugStud(){
        for (int i = 0; i < 3; i++) {
            StudentView studentView = new StudentView(i, "student", "yellow", StudentView.studentSize);
            studentView.setEnabled();
            int finalI = i;
            studentView.setCallback(mouseEvent -> {
                System.out.println("TEST " + finalI);
                studentView.setDisabled();
            });
            studentPane.add(studentView, i, i);
            StudentContainerPane.setHalignment(studentView, HPos.CENTER);
        }
    }
}
