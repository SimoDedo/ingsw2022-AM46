package it.polimi.ingsw.View.GUI.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that models a generic student container pane. It extends a generic grid but is created with a rigid structure
 * based on the given parameters.
 */
public class StudentContainerPane extends GridPane {

    /**
     * Constructor for a student container pane with the specified parameters
     * @param containerType The type of container that this student container pain belongs to
     * @param id The id container that this student container pain belongs to
     * @param parentWidth The width container that this student container pain belongs to
     * @param parentHeight The height container that this student container pain belongs to
     * @param widthPct the percentage of the parent's width that this student container will occupy
     * @param rows the number of rows
     * @param columns the number of columns
     * @param paddingHPct the left and right padding, as percentage of the parent's width
     * @param paddingVPct the top and bottom padding, as percentage of the parent's height
     * @param paddingExtraRightPct an additional extra padding on the right, needed for certain grids
     */

    public StudentContainerPane(String containerType, int id, double parentWidth, double parentHeight,
                                double widthPct, int rows, int columns, double paddingHPct, double paddingVPct, double paddingExtraRightPct) {
        this.setId(containerType+id);
        this.setAlignment(Pos.CENTER);
        for (int i = 0; i < rows; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(100.0 / rows);
            this.getRowConstraints().add(r);
        }
        for (int i = 0; i < columns; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(100.0 / columns);
            this.getColumnConstraints().add(c);
        }
        this.setPadding(new Insets(parentHeight * (paddingVPct/100), parentWidth * (paddingHPct/100 + paddingExtraRightPct/100) * (widthPct/100),
                parentHeight * (paddingVPct/100), parentWidth * (paddingHPct/100) * (widthPct/100)));

    }

    /**
     * Getter for all the students contained in this student container.
     * @return a list of all the student views contained.
     */
    public List<StudentView> getStudents() {
        List<StudentView> retval = new ArrayList<>();
        for (Node node : this.getChildren()) {
            retval.add((StudentView) node);
        }
        return retval;
    }
}
