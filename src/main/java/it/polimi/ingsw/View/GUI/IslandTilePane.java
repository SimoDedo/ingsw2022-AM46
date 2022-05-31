package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class IslandTilePane extends StackPane {

    boolean partyMode = true;
    int index;

    static double size = 120.0, shrinkConstant = 0.80, modifierSize = 25.0, studentSize = 15.0;

    private TowerColor towerColor;

    private Point2D forwardMergePoint, backMergePoint;

    public IslandTilePane(int index) {
        this.index = index;

        Image islandTileBackground = new Image("/world/islandtile" + ThreadLocalRandom.current().nextInt(1, partyMode ? 5 : 4) + ".png"
                , 300, 300, true, true);
        ImageView imageView = new ImageView(islandTileBackground);
        imageView.setRotate(60 * ThreadLocalRandom.current().nextInt(partyMode ? 6 : 1));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(size);
        imageView.setSmooth(true);
        imageView.setCache(true);
        this.getChildren().add(imageView);
        IslandTilePane.setAlignment(imageView, Pos.CENTER);
        VBox gridContainer = new VBox(5);
        this.getChildren().add(gridContainer);
        gridContainer.setAlignment(Pos.TOP_CENTER);
        gridContainer.setPrefSize(100.0, 80);

        // gridpane for tower, mother nature, no entry tile goes here
        GridPane islandModifiersPane = new GridPane();
        islandModifiersPane.setAlignment(Pos.CENTER);
        gridContainer.getChildren().add(islandModifiersPane);
        islandModifiersPane.setPrefSize(100.0, 25.0);

        PawnView tower = new PawnView(0, "tower", "white", PawnView.pawnSize);
        islandModifiersPane.add(tower, 0, 0);
        PawnView mothernature = new PawnView(0, "mothernature", "", PawnView.pawnSize);
        islandModifiersPane.add(mothernature, 1, 0);
        PawnView noentry = new PawnView(0, "noentrytile", "", PawnView.pawnSize);
        islandModifiersPane.add(noentry, 2, 0);

        // gridpane for students goes here
        GridPane studentPane = new GridPane();
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        gridContainer.getChildren().add(studentPane);

        for (int i = 0; i < 4; i++) {
            StudentView studentView = new StudentView(i, "student", "red", StudentView.studentSize);
            studentView.setEnabled();
            int finalI = i;
            studentView.setCallback(mouseEvent -> {
                System.out.println("TEST " + finalI);
                studentView.setDisabled();
            });
            studentPane.add(studentView, i, i);
        }
    }

    public Point2D getForwardMergePoint() {
        int side;
        switch (index) {
            case 0, 11 -> side = 0;
            case 1, 2 -> side = 1;
            case 3, 4 -> side = 2;
            case 5, 6 -> side = 3;
            case 7, 8 -> side = 4;
            default -> side = 5;
        }
        Point2D mergePointDiff = new Point2D(
                Math.cos(Math.PI/2 + Math.PI/3*side)*size/2*shrinkConstant,
                - Math.sin(Math.PI/2 + Math.PI/3*side)*size/2*shrinkConstant
        );
        forwardMergePoint = getCenter().add(mergePointDiff);
        return forwardMergePoint;
    }

    public Point2D getBackMergePoint() {
        int side;
        switch (index) {
            case 0, 1 -> side = 3;
            case 2, 3 -> side = 4;
            case 4, 5 -> side = 5;
            case 6, 7 -> side = 0;
            case 8, 9 -> side = 1;
            default -> side = 2;
        }
        Point2D mergePointDiff = new Point2D(
                Math.cos(Math.PI/2 + Math.PI/3*side)*size/2*shrinkConstant,
                - Math.sin(Math.PI/2 + Math.PI/3*side)*size/2*shrinkConstant
        );
        backMergePoint = getCenter().add(mergePointDiff);
        return backMergePoint;
    }

    public Point2D getCenter() {
        // return new Point2D(getLayoutX() + size / 2, getLayoutY() + size / 2);
        return new Point2D(localToParent(0,0).getX() + size / 2, localToParent(0,0).getY() + size / 2);
    }
}
