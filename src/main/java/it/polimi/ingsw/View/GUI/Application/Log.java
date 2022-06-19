package it.polimi.ingsw.View.GUI.Application;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an extension of VBox that works as a log and displays information (such as game updates or expected moves)
 * in the upper right part of the screen.
 */
public class Log extends VBox {

    /**
     * The number of lines for this log.
     */
    private final int numOfLines = 3;

    /**
     * A list of the lines of this log.
     */
    private final List<Label> labelList = new ArrayList<>();

    /**
     * A list of the fade-out timelines associated with each line in this log.
     */
    private final List<Timeline> timelineList = new ArrayList<>();

    /**
     * Creates a number of empty log lines and timelines equal to numOfLines.
     */
    public Log() {
        this.setId("log");
        this.setAlignment(Pos.CENTER_RIGHT);

        for (int i = 0; i < numOfLines; i++) {
            Label label = new Label();
            label.setId("logLabel" + i);
            if (i == numOfLines - 1) label.setStyle("-fx-font-size: 12;");
            else label.setStyle("-fx-font-size: 11;");

            labelList.add(i, label);
            timelineList.add(i, new Timeline());

            this.getChildren().add(label);
        }
    }

    /**
     * Pushes a new line to the log, that is, it shifts the lines of text towards the top, deletes the first row, and inserts
     * the given message in the last row. It also starts the fade-out animation for the rows that aren't the last one.
     * @param message the new message to append to the log
     */
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
        // fadeOut(labelList.get(i), timelineList.get(i), 1.0); // keeps last log message visible
    }

    /**
     * Utility function for managing the fade-out animation for a given log line. The initial opacity of the line and the
     * duration of the animation both depend on the startingOpacity parameter.
     * @param label the Label object on which to apply the animation
     * @param timeline the animation to set up
     * @param startingOpacity the initial opacity of the line
     */
    private void fadeOut(Label label, Timeline timeline, double startingOpacity) {

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
