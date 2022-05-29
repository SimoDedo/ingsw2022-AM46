package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class CloudPane extends StackPane {

    public CloudPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(75.0, 75.0);
        Image cloud = new Image("/clouds/cloud_card_" + ThreadLocalRandom.current().nextInt(0, 5) + ".png",
                200, 200, true, true);
        ImageView cloudView = new ImageView(cloud);
        cloudView.setPreserveRatio(true);
        cloudView.setFitHeight(75.0);
        cloudView.setSmooth(true);
        cloudView.setCache(true);
        this.getChildren().add(cloudView);

        GridPane studentPane = new GridPane();
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        this.getChildren().add(studentPane);

        double studentSize = 15.0;

        Image red = new Image("/pawns/student_pink.png", 50, 50, true, true);
        ImageView redView = new ImageView(red);
        redView.setEffect(new DropShadow(10.0, Color.WHITE));
        redView.setPreserveRatio(true);
        redView.setFitHeight(studentSize);
        redView.setSmooth(true);
        redView.setCache(true);
        studentPane.add(redView, 2, 0);

        Image red1 = new Image("/pawns/student_red.png", 50, 50, true, true);
        ImageView redView1 = new ImageView(red1);
        redView1.setEffect(new DropShadow(10.0, Color.WHITE));
        redView1.setPreserveRatio(true);
        redView1.setFitHeight(studentSize);
        redView1.setSmooth(true);
        redView1.setCache(true);
        studentPane.add(redView1, 0, 0);

        Image red2 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView2 = new ImageView(red2);
        redView2.setEffect(new DropShadow(10.0, Color.WHITE));
        redView2.setPreserveRatio(true);
        redView2.setFitHeight(studentSize);
        redView2.setSmooth(true);
        redView2.setCache(true);
        studentPane.add(redView2, 1, 1);

        Image red3 = new Image("/pawns/student_green.png", 50, 50, true, true);
        ImageView redView3 = new ImageView(red3);
        redView3.setEffect(new DropShadow(10.0, Color.WHITE));
        redView3.setPreserveRatio(true);
        redView3.setFitHeight(studentSize);
        redView3.setSmooth(true);
        redView3.setCache(true);
        studentPane.add(redView3, 2, 2);

        Image red4 = new Image("/pawns/student_blue.png", 50, 50, true, true);
        ImageView redView4 = new ImageView(red4);
        redView4.setEffect(new DropShadow(10.0, Color.WHITE));
        redView4.setPreserveRatio(true);
        redView4.setFitHeight(studentSize);
        redView4.setSmooth(true);
        redView4.setCache(true);
        studentPane.add(redView4, 3, 2);
    }
}
