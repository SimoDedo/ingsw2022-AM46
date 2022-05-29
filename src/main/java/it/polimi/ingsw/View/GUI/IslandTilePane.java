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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class IslandTilePane extends StackPane {

    boolean partyMode = false;

    static double size = 120.0, shrinkConstant = 0.80, modifierSize = 25.0, studentSize = 15.0;

    private TowerColor towerColor;

    private Point2D forwardMergePoint, backMergePoint;

    public IslandTilePane() {
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

        Image tower = new Image("/pawns/tower_white1.png", 50, 50, true, true);
        ImageView towerView = new ImageView(tower);
        towerView.setEffect(new DropShadow(20.0, Color.WHITE));
        towerView.setPreserveRatio(true);
        towerView.setFitHeight(modifierSize);
        towerView.setSmooth(true);
        towerView.setCache(true);
        islandModifiersPane.add(towerView, 0, 0);

        Image mother = new Image("/pawns/mothernature0.png", 50, 50, true, true);
        ImageView motherView = new ImageView(mother);
        motherView.setEffect(new DropShadow(20.0, Color.WHITE));
        motherView.setPreserveRatio(true);
        motherView.setFitHeight(modifierSize);
        motherView.setSmooth(true);
        motherView.setCache(true);
        islandModifiersPane.add(motherView, 1, 0);

        Image noentry = new Image("/pawns/noentrytile.png", 50, 50, true, true);
        ImageView noentryView = new ImageView(noentry);
        noentryView.setEffect(new DropShadow(20.0, Color.WHITE));
        noentryView.setPreserveRatio(true);
        noentryView.setFitHeight(modifierSize);
        noentryView.setSmooth(true);
        noentryView.setCache(true);
        islandModifiersPane.add(noentryView, 2, 0);

        // gridpane for students goes here
        GridPane studentPane = new GridPane();
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        gridContainer.getChildren().add(studentPane);
        Image red = new Image("/pawns/student_pink.png", 50, 50, true, true);
        ImageView redView = new ImageView(red);
        redView.setEffect(new DropShadow(10.0, Color.BLACK));
        redView.setPreserveRatio(true);
        redView.setFitHeight(studentSize);
        redView.setSmooth(true);
        redView.setCache(true);
        studentPane.add(redView, 2, 0);

        Image red1 = new Image("/pawns/student_red.png", 50, 50, true, true);
        ImageView redView1 = new ImageView(red1);
        redView1.setEffect(new DropShadow(10.0, Color.BLACK));
        redView1.setPreserveRatio(true);
        redView1.setFitHeight(studentSize);
        redView1.setSmooth(true);
        redView1.setCache(true);
        studentPane.add(redView1, 0, 0);

        Image red2 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView2 = new ImageView(red2);
        redView2.setEffect(new DropShadow(10.0, Color.BLACK));
        redView2.setPreserveRatio(true);
        redView2.setFitHeight(studentSize);
        redView2.setSmooth(true);
        redView2.setCache(true);
        studentPane.add(redView2, 1, 1);

        Image red3 = new Image("/pawns/student_green.png", 50, 50, true, true);
        ImageView redView3 = new ImageView(red3);
        redView3.setEffect(new DropShadow(10.0, Color.BLACK));
        redView3.setPreserveRatio(true);
        redView3.setFitHeight(studentSize);
        redView3.setSmooth(true);
        redView3.setCache(true);
        studentPane.add(redView3, 2, 2);

        Image red4 = new Image("/pawns/student_blue.png", 50, 50, true, true);
        ImageView redView4 = new ImageView(red4);
        redView4.setEffect(new DropShadow(10.0, Color.BLACK));
        redView4.setPreserveRatio(true);
        redView4.setFitHeight(studentSize);
        redView4.setSmooth(true);
        redView4.setCache(true);
        studentPane.add(redView4, 3, 2);
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
