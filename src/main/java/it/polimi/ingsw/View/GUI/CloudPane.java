package it.polimi.ingsw.View.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class CloudPane extends StackPane {

    public static double cloudSize = 100.0;

    public CloudPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(cloudSize, cloudSize);
        Image cloud = new Image("/clouds/cloud_card_" + ThreadLocalRandom.current().nextInt(0, 5) + ".png",
                200, 200, true, true);
        ImageView cloudView = new ImageView(cloud);
        cloudView.setEffect(new DropShadow());
        cloudView.setPreserveRatio(true);
        cloudView.setFitHeight(75.0);
        cloudView.setSmooth(true);
        cloudView.setCache(true);
        this.getChildren().add(cloudView);

        StudentContainerPane studentPane = new StudentContainerPane(cloudSize, cloudSize, 100, 3, 3, 25.0, 25.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        this.getChildren().add(studentPane);

        /* debug
        for (int i = 0; i < 3; i++) {
            StudentView studentView = new StudentView(i, "student", "red", StudentView.studentSize);
            studentView.setEnabled();
            int finalI = i;
            studentView.setCallback(mouseEvent -> {
                System.out.println("TEST " + finalI);
                studentView.setDisabled();
            });
            studentPane.add(studentView, i, i);
            StudentContainerPane.setHalignment(studentView, HPos.CENTER);
        }

         */
    }
}
