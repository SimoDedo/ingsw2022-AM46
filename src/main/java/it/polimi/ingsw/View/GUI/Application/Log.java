package it.polimi.ingsw.View.GUI.Application;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.control.Label;
import javafx.util.Duration;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class Log extends VBox {

    private final double logWidth = 400.0, logHeight = 100.0;

    private final List<Label> labelList = new ArrayList<>();

    private final List<Timeline> timelineList = new ArrayList<>();

    public Log() {
        this.setId("log");
        this.setAlignment(Pos.CENTER_RIGHT);

        for (int i = 0; i < 3; i++) {
            Label label = new Label();
            label.setId("logLabel" + i);
            label.setStyle("-fx-font-size: 11;");

            labelList.add(i, label);
            timelineList.add(i, new Timeline());

            this.getChildren().add(label);
        }
    }

    public void push(String message) {
        int i;
        for (i = 0; i < labelList.size()-1; i++) {
            Label currentLabel = labelList.get(i);
            Timeline currentTimeline = timelineList.get(i);
            Label nextLabel = labelList.get(i+1);
            currentLabel.setText(nextLabel.getText());
            fadeOut(currentLabel, currentTimeline, nextLabel.getOpacity());
        }
        labelList.get(i).setText(message + "...");
        fadeOut(labelList.get(i), timelineList.get(i), 1.0);
    }

    private void fadeOut(Label label, Timeline timeline, double startingOpacity) {
        System.out.println("fadeout called " + label.getId() + " " + startingOpacity);

        timeline.setOnFinished(event -> {});
        timeline.stop();

        DoubleProperty opacity = label.opacityProperty();
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, startingOpacity)),
                new KeyFrame(new Duration(10000*(startingOpacity+0.5)/1.5), new KeyValue(opacity, 0.0))
        );
        timeline.play();
    }
}
