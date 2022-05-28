package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;

public class ArchipelagoPane extends AnchorPane {

    public ArchipelagoPane() {
        this.setId("archipelagoPane");
        double archipelagoSide = 500.0;
        this.setPrefSize(archipelagoSide, archipelagoSide);
        double centerPos = archipelagoSide / 2.0;

        for (int i = 0; i < 12; i++) {
            IslandTilePane newIsland = new IslandTilePane();
            newIsland.setId("islandTilePane" + i);
            this.getChildren().add(newIsland);
            newIsland.relocate(centerPos + Math.cos(Math.PI / 6 * i)*centerPos, centerPos - Math.sin(Math.PI / 6 * i)*centerPos);
            newIsland.setMergePoints(i);
        }
        createEmptyBridges();

        // character hbox goes here
        double charContainerHeight = 80.0, charContainerWidth = 300.0;
        HBox charContainer = new HBox(5.0);
        this.getChildren().add(charContainer);
        charContainer.setAlignment(Pos.CENTER);
        charContainer.setPrefSize(charContainerHeight, charContainerWidth);
        charContainer.relocate(centerPos - 40.0, centerPos - 200.0);

        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = new CharacterPane();
            characterPane.setId("characterPane" + i);
            charContainer.getChildren().add(characterPane);
            characterPane.setCharacterImage(i+5);
        }

        // cloud hbox goes here
        double cloudContainerHeight = 80.0, cloudContainerWidth = 350.0;
        HBox cloudContainer = new HBox(5.0);
        cloudContainer.setAlignment(Pos.CENTER);
        cloudContainer.setPrefSize(cloudContainerWidth, cloudContainerHeight);
        cloudContainer.setMaxSize(cloudContainerWidth, cloudContainerHeight);
        this.getChildren().add(cloudContainer);
        cloudContainer.relocate(centerPos - 110.0, centerPos + 30.0);

        for (int i = 0; i < 4; i++) {
            CloudPane cloudPane = new CloudPane();
            cloudPane.setId("cloudPane" + i);
            cloudContainer.getChildren().add(cloudPane);
        }

        // bag goes here
        double bagSize = 60.0;
        StackPane bagPane = new StackPane();
        bagPane.setEffect(new DropShadow(50.0, Color.WHITE));
        bagPane.setAlignment(Pos.CENTER);
        bagPane.setMaxSize(bagSize, bagSize);
        Image bag = new Image("/world/bag_students.png");
        ImageView bagView = new ImageView(bag);
        bagView.setPreserveRatio(true);
        bagView.setFitHeight(bagSize);
        bagView.setSmooth(true);
        bagView.setCache(true);
        bagPane.getChildren().add(bagView);
        this.getChildren().add(bagPane);
        bagPane.relocate(230.0, 400.0);

        // coinheap goes here
        StackPane coinBagPane = new StackPane();
        coinBagPane.setEffect(new DropShadow(50.0, Color.WHITE));
        coinBagPane.setAlignment(Pos.CENTER);
        coinBagPane.setMaxSize(bagSize, bagSize);
        Image coinBag = new Image("/world/bag_coins.png");
        ImageView coinBagView = new ImageView(coinBag);
        coinBagView.setPreserveRatio(true);
        coinBagView.setFitHeight(bagSize);
        coinBagView.setSmooth(true);
        coinBagView.setCache(true);
        coinBagPane.getChildren().add(coinBagView);
        this.getChildren().add(coinBagPane);
        coinBagPane.relocate(340.0, 400.0);
    }

    public Point2D calcMergeDiff(int forwardIndex, int backIndex) {
        IslandTilePane backIsland = (IslandTilePane) this.lookup("#islandTilePane" + backIndex);
        Point2D backMergePoint = backIsland.getBackMergePoint();
        IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + forwardIndex);
        Point2D forwardMergePoint = forwardIsland.getForwardMergePoint();
        System.out.println("backMergePoint = " + backMergePoint);
        System.out.println("forwardMergePoint = " + forwardMergePoint);
        System.out.println("midpoint = " + backMergePoint.midpoint(forwardMergePoint));
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
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(translate.xProperty(), 0d),
                        new KeyValue(translate.yProperty(), 0d)),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(translate.xProperty(), mergeDiff.getX()),
                        new KeyValue(translate.yProperty(), mergeDiff.getY()))
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
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(translate.xProperty(), 0d),
                        new KeyValue(translate.yProperty(), 0d)),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(translate.xProperty(), reverseMergeDiff.getX()),
                        new KeyValue(translate.yProperty(), reverseMergeDiff.getY()))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void createEmptyBridges() {
        double bridgeHeight = 50.0, bridgeLength = 25.0, angleAdjustment = 90.0;
        for (int i = 0; i < 12; i++) {
            int forwardIndex = i, backIndex = i!=11 ? i+1 : 0;
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
        bridgeView.setImage(new Image("/world/" + pngName + ThreadLocalRandom.current().nextInt(1, 3) + ".png"));
        DoubleProperty opacity = bridgeView.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

}
