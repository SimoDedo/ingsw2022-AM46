package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.List;

public class ArchipelagoPane extends AnchorPane {

    private final HBox cloudContainer;
    private final HBox charContainer;

    private final double archipelagoSide = 500.0;
    private final double centerPos = archipelagoSide / 2.0;

    public ArchipelagoPane() {
        this.setId("archipelagoPane");
        this.setPrefSize(archipelagoSide, archipelagoSide);

        for (int i = 0; i < 12; i++) {
            IslandTilePane newIsland = new IslandTilePane(i);
            newIsland.setId("islandTilePane" + i);
            this.getChildren().add(newIsland);
            newIsland.relocate(centerPos +  Math.cos(Math.PI / 6 * i)*centerPos, centerPos - Math.sin(Math.PI / 6 * i)*centerPos);
        }
        createEmptyBridges();

        // character hbox goes here
        double charContainerHeight = 80.0, charContainerWidth = 300.0;
        charContainer = new HBox(5.0);
        this.getChildren().add(charContainer);
        charContainer.setAlignment(Pos.CENTER);
        charContainer.setPrefSize(charContainerWidth, charContainerHeight);
        charContainer.relocate(centerPos - 90.0, centerPos + 50.0);


        // cloud hbox goes here
        double cloudContainerHeight = CloudPane.cloudSize, cloudContainerWidth = CloudPane.cloudSize*4.5;
        cloudContainer = new HBox(5.0);
        cloudContainer.setAlignment(Pos.CENTER);
        cloudContainer.setPrefSize(cloudContainerWidth, cloudContainerHeight);
        cloudContainer.setMaxSize(cloudContainerWidth, cloudContainerHeight);
        this.getChildren().add(cloudContainer);
        cloudContainer.relocate(centerPos - 160.0, centerPos + 360.0);

        // bag goes here
        BagPane studentsBagPane = new BagPane("students");
        this.getChildren().add(studentsBagPane);
        studentsBagPane.relocate(centerPos - 5.0, centerPos - 40.0);

    }

    public void createIslands(List<Integer> islandIDs, int motherNatureIsland){
        //Finishes creating islands
        for (int i = 0; i < 12; i++) {
            IslandTilePane islandTilePane = (IslandTilePane) this.lookup("#islandTilePane" + i);
            islandTilePane.createIslandTile(islandIDs.get(i));
            if(islandIDs.get(i) == motherNatureIsland)
                islandTilePane.putMotherNature();
        }
    }

    public void createClouds(int numOfPlayers,List<Integer> cloudIDs){
        for (int i = 0; i < numOfPlayers; i++) {
            CloudPane cloudPane = new CloudPane();
            cloudPane.setId("cloudPane" + i);
            cloudContainer.getChildren().add(cloudPane);
            cloudPane.createCloud(cloudIDs.get(i));
        }
    }

    public void createCharacterAndHeap(List<Integer> characterIDs){
        //Creates characters
        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = new CharacterPane();
            characterPane.setId("characterPane" + i);
            charContainer.getChildren().add(characterPane);
            characterPane.createCharacter(characterIDs.get(i));

        }
        // coinheap goes here
        BagPane coinsBagPane = new BagPane("coins");
        this.getChildren().add(coinsBagPane);
        coinsBagPane.relocate(centerPos + 65.0, centerPos - 40.0);
    }

    public Point2D calcMergeDiff(int forwardIndex, int backIndex) {
        IslandTilePane backIsland = (IslandTilePane) this.lookup("#islandTilePane" + backIndex);
        Point2D backMergePoint = backIsland.getBackMergePoint();
        IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + forwardIndex);
        Point2D forwardMergePoint = forwardIsland.getForwardMergePoint();
        return backMergePoint.midpoint(forwardMergePoint).subtract(forwardMergePoint);
    }

    public void relocateForward(int index, Point2D mergeDiff) {
        IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + index);
        // standard version:
        /*forwardIsland.relocate(
                forwardIsland.getLayoutX() + mergeDiff.getX(),
                forwardIsland.getLayoutY() + mergeDiff.getY());*/

        // translate version:
        Translate translate = new Translate();
        forwardIsland.getTransforms().add(translate);
        Interpolator customInterpolator = new Interpolator() {
            @Override
            protected double curve(double v) {
                return Math.pow(v, 10);
            }
        };
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(translate.xProperty(), 0d, customInterpolator),
                        new KeyValue(translate.yProperty(), 0d, customInterpolator)),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(translate.xProperty(), mergeDiff.getX(), customInterpolator),
                        new KeyValue(translate.yProperty(), mergeDiff.getY(), customInterpolator))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void relocateBack(int index, Point2D mergeDiff) {
        Point2D reverseMergeDiff = new Point2D(- mergeDiff.getX(), - mergeDiff.getY());
        IslandTilePane backIsland = (IslandTilePane) this.lookup("#islandTilePane" + index);
        // standard version:
        /*backIsland.relocate(
                backIsland.getLayoutX() + reverseMergeDiff.getX(),
                backIsland.getLayoutY() + reverseMergeDiff.getY());*/

        //translate version:
        Translate translate = new Translate();
        backIsland.getTransforms().add(translate);

        Interpolator customInterpolator = new Interpolator() {
            @Override
            protected double curve(double v) {
                return Math.pow(v, 10);
            }
        };
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(translate.xProperty(), 0d, customInterpolator),
                        new KeyValue(translate.yProperty(), 0d, customInterpolator)),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(translate.xProperty(), reverseMergeDiff.getX(), customInterpolator),
                        new KeyValue(translate.yProperty(), reverseMergeDiff.getY(), customInterpolator))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void createEmptyBridges() {
        double bridgeHeight = 50.0, bridgeLength = 25.0, angleAdjustment = 90.0;
        for (int forwardIndex = 0; forwardIndex < 12; forwardIndex++) {
            int backIndex = forwardIndex!=11 ? forwardIndex+1 : 0;
            Image bridge = new Image("/world/emptybridge.png");
            ImageView bridgeView = new ImageView(bridge);
            bridgeView.setId("bridgeView" + forwardIndex);

            bridgeView.setPreserveRatio(true);
            bridgeView.setFitHeight(bridgeHeight);
            bridgeView.setSmooth(true);
            bridgeView.setCache(true);
            this.getChildren().add(bridgeView);

            IslandTilePane backIsland = (IslandTilePane) this.lookup("#islandTilePane" + backIndex);
            Point2D backCenter = backIsland.getCenter();
            IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + forwardIndex);
            Point2D forwardCenter = forwardIsland.getCenter();
            Point2D midpoint = backCenter.midpoint(forwardCenter);

            double angle = Math.atan2(
                    forwardCenter.getY() - backCenter.getY(),
                    forwardCenter.getX() - backCenter.getX())
                    * 180 / Math.PI + angleAdjustment;

            bridgeView.relocate(midpoint.getX() - bridgeLength / 2, midpoint.getY() - bridgeHeight / 2);
            bridgeView.setRotate(angle);
        }
    }

    public void setBridge(int forwardIndex, TowerColor bridgeColor) {
        ImageView bridgeView = (ImageView) this.lookup("#bridgeView" + forwardIndex);
        String pngName;
        switch (bridgeColor) {
            case WHITE -> pngName = "sandstone";
            case GREY -> pngName = "stone";
            default -> pngName = "wood";
        }
        bridgeView.setImage(new Image("/world/" + pngName + "1.png",
                200, 200, true, true));
        DoubleProperty opacity = bridgeView.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

    public void debugStud(){
        for (int i = 0; i < 12; i++) {
            IslandTilePane islandTilePane = (IslandTilePane) this.lookup("#islandTilePane" + i);
            islandTilePane.debugStud();
        }
        for (int i = 0; i < 4; i++) {
            CloudPane cloudPane = (CloudPane) this.lookup("#cloudPane" + i);
            if(cloudPane != null)
                cloudPane.debugStud();
        }
        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + i);
            if(characterPane != null)
                characterPane.debugStud();
        }
    }

}
