package it.polimi.ingsw.View.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class StudentContainerPane extends GridPane {

    public StudentContainerPane(double parentWidth, double parentHeight, double widthPct, int rows, int columns, double paddingHPct,
                                double paddingVPct, double paddingExtraRightPct) {
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
        this.setPadding(new Insets(parentHeight * (paddingVPct/100), parentWidth * (paddingHPct/100 + paddingExtraRightPct/100) * widthPct,
                parentHeight * (paddingVPct/100), parentWidth * (paddingHPct/100) * widthPct));

    }

}
