package it.polimi.ingsw.View.GUI;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class IslandTilePane extends StackPane {

    boolean partyMode = false;

    static double size = 120.0, shrinkConstant = 0.80;

    Point2D forwardMergePoint, backMergePoint;

    public IslandTilePane() {
        Image islandTileBackground = new Image("/world/islandtile" + ThreadLocalRandom.current().nextInt(1, partyMode ? 5 : 4) + ".png");
        ImageView imageView = new ImageView(islandTileBackground);
        imageView.setRotate(60 * ThreadLocalRandom.current().nextInt(partyMode ? 6 : 1));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(size);
        imageView.setSmooth(true);
        imageView.setCache(true);
        this.getChildren().add(imageView);
        IslandTilePane.setAlignment(imageView, Pos.CENTER);

        // gridpane for tower, mother nature, no entry tile goes here

        // gridpane for students goes here
    }

    public void setMergePoints(int index) {
        List<Point2D> mergePointDiffList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            mergePointDiffList.add(new Point2D(
                    Math.cos(Math.PI/2 + Math.PI/3*i)*size/2*shrinkConstant,
                    - Math.sin(Math.PI/2 + Math.PI/3*i)*size/2*shrinkConstant
            ));
        }

        switch (index) {
            case 0 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(0));
                backMergePoint = getCenter().add(mergePointDiffList.get(3));
            }
            case 1 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(1));
                backMergePoint = getCenter().add(mergePointDiffList.get(3));
            }
            case 2 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(1));
                backMergePoint = getCenter().add(mergePointDiffList.get(4));
            }
            case 3 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(2));
                backMergePoint = getCenter().add(mergePointDiffList.get(4));
            }
            case 4 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(2));
                backMergePoint = getCenter().add(mergePointDiffList.get(5));
            }
            case 5 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(3));
                backMergePoint = getCenter().add(mergePointDiffList.get(5));
            }
            case 6 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(3));
                backMergePoint = getCenter().add(mergePointDiffList.get(0));
            }
            case 7 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(4));
                backMergePoint = getCenter().add(mergePointDiffList.get(0));
            }
            case 8 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(4));
                backMergePoint = getCenter().add(mergePointDiffList.get(1));
            }
            case 9 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(5));
                backMergePoint = getCenter().add(mergePointDiffList.get(1));
            }
            case 10 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(5));
                backMergePoint = getCenter().add(mergePointDiffList.get(2));
            }
            case 11 -> {
                forwardMergePoint = getCenter().add(mergePointDiffList.get(0));
                backMergePoint = getCenter().add(mergePointDiffList.get(2));
            }
        }
    }

    public Point2D getForwardMergePoint() {
        return forwardMergePoint;
    }

    public Point2D getBackMergePoint() {
        return backMergePoint;
    }

    public Point2D getCenter() {
        return new Point2D(getLayoutX() + size / 2, getLayoutY() + size / 2);
        // return new Point2D(localToParent(0,0).getX() + size / 2, localToParent(0,0).getY() + size / 2);
    }
}
