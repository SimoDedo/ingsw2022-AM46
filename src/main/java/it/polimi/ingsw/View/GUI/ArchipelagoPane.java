package it.polimi.ingsw.View.GUI;

import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class ArchipelagoPane extends AnchorPane {

    public ArchipelagoPane() {
        this.setId("archipelagoPane");
        double archipelagoSide = 500.0;
        this.setPrefSize(archipelagoSide, archipelagoSide);
        double centerPos = archipelagoSide / 2.0;

        for (int i = 0; i < 12; i++) {
            IslandTilePane newIsland = new IslandTilePane();
            this.getChildren().add(newIsland);
            newIsland.relocate(centerPos + Math.cos(Math.PI / 6 * i)*centerPos, centerPos - Math.sin(Math.PI / 6 * i)*centerPos);
            newIsland.setMergePoints(i);
        }

        // character hbox goes here

        // cloud hbox goes here

        // bag goes here

        // coinheap goes here

    }

    public Point2D calcMergeDiff(int forwardIndex, int backIndex) {
        IslandTilePane backIsland = (IslandTilePane) this.getChildren().get(backIndex);
        Point2D backMergePoint = backIsland.getBackMergePoint();
        IslandTilePane forwardIsland = (IslandTilePane) this.getChildren().get(forwardIndex);
        Point2D forwardMergePoint = forwardIsland.getForwardMergePoint();

        return backMergePoint.midpoint(forwardMergePoint).subtract(forwardMergePoint);
    }

    public void relocateForward(int index, Point2D mergeDiff) {
        IslandTilePane forwardIsland = (IslandTilePane) this.getChildren().get(index);
        forwardIsland.relocate(
                forwardIsland.getLayoutX() + mergeDiff.getX(),
                forwardIsland.getLayoutY() + mergeDiff.getY());

//        Path path = new Path();
//        getChildren().add(path);
//        path.getElements().add(new MoveTo(
//                forwardIsland.getLayoutX() + mergeDiff.getX(),
//                forwardIsland.getLayoutY() + mergeDiff.getY()));
//        PathTransition transition = new PathTransition();
//        transition.setDuration(Duration.seconds(5));
//        transition.setDelay(Duration.seconds(5));
//        transition.setPath(path);
//        transition.setNode(forwardIsland);
//        transition.setCycleCount(2);
//        transition.setAutoReverse(true);
//        transition.play();
    }

    public void relocateBack(int index, Point2D mergeDiff) {
        Point2D reverseMergeDiff = new Point2D(- mergeDiff.getX(), - mergeDiff.getY());
        IslandTilePane backIsland = (IslandTilePane) this.getChildren().get(index);
        backIsland.relocate(
                backIsland.getLayoutX() + reverseMergeDiff.getX(),
                backIsland.getLayoutY() + reverseMergeDiff.getY());

//        Path path = new Path();
//        getChildren().add(path);
//        path.getElements().add(new MoveTo(
//                backIsland.getLayoutX() + mergeDiff.getX(),
//                backIsland.getLayoutY() + mergeDiff.getY()));
//        PathTransition transition = new PathTransition();
//        transition.setDuration(Duration.seconds(5));
//        transition.setDelay(Duration.seconds(5));
//        transition.setPath(path);
//        transition.setNode(backIsland);
//        transition.setCycleCount(2);
//        transition.setAutoReverse(true);
//        transition.play();
    }

}
