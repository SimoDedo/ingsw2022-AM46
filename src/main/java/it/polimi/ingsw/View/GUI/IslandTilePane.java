package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.ThreadLocalRandom;

public class IslandTilePane extends StackPane {

    boolean partyMode = false;
    int index;

    static double islandTileSize = 120.0, shrinkConstant = 0.80, modifierSize = 25.0, studentSize = 15.0;

    private TowerColor towerColor;

    private Point2D forwardMergePoint, backMergePoint;

    private GridPane islandModifiersPane;
    private final VBox gridContainer;
    private StudentContainerPane studentPane;

    public IslandTilePane(int index) {
        this.index = index;

        Image islandTileBackground = new Image("/world/islandtile" + ThreadLocalRandom.current().nextInt(1, partyMode ? 5 : 4) + ".png",
                300, 300, true, true);
        ImageView imageView = new ImageView(islandTileBackground);
        imageView.setRotate(60 * ThreadLocalRandom.current().nextInt(partyMode ? 6 : 1));
        imageView.setEffect(new DropShadow());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(islandTileSize);
        imageView.setSmooth(true);
        imageView.setCache(true);
        this.getChildren().add(imageView);

        IslandTilePane.setAlignment(imageView, Pos.CENTER);
        gridContainer = new VBox(islandTileSize *0.05);
        this.getChildren().add(gridContainer);
        gridContainer.setAlignment(Pos.TOP_CENTER);
        gridContainer.setPrefSize(islandTileSize, islandTileSize);

        // gridpane for tower, mother nature, no entry tile goes here
        islandModifiersPane = new GridPane();
        islandModifiersPane.setAlignment(Pos.CENTER);
        gridContainer.getChildren().add(islandModifiersPane);
        islandModifiersPane.setPrefSize(islandTileSize, islandTileSize *0.15);

        /* debug
        PawnView tower = new PawnView(0, "tower", "white", PawnView.pawnSize);
        islandModifiersPane.add(tower, 0, 0);
        PawnView mothernature = new PawnView(0, "mothernature", "", PawnView.pawnSize);
        islandModifiersPane.add(mothernature, 1, 0);
        PawnView noentry = new PawnView(0, "noentrytile", "", PawnView.pawnSize);
        islandModifiersPane.add(noentry, 2, 0);
        */
    }

    public void createIslandTile(int ID){
        studentPane = new StudentContainerPane("islandStudentsPane", ID,
                islandTileSize, islandTileSize *0.8, 100, 4, 4, 25.0, 0.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        gridContainer.getChildren().add(studentPane);
    }

    public void putMotherNature(){
        PawnView motherNature = new PawnView(0, "mothernature", "", PawnView.pawnSize);
        islandModifiersPane.add(motherNature, 1, 0);
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
                Math.cos(Math.PI/2 + Math.PI/3*side)* islandTileSize /2*shrinkConstant,
                - Math.sin(Math.PI/2 + Math.PI/3*side)* islandTileSize /2*shrinkConstant
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
                Math.cos(Math.PI/2 + Math.PI/3*side)* islandTileSize /2*shrinkConstant,
                - Math.sin(Math.PI/2 + Math.PI/3*side)* islandTileSize /2*shrinkConstant
        );
        backMergePoint = getCenter().add(mergePointDiff);
        return backMergePoint;
    }

    public Point2D getCenter() {
        // return new Point2D(getLayoutX() + size / 2, getLayoutY() + size / 2);
        return new Point2D(localToParent(0,0).getX() + islandTileSize / 2, localToParent(0,0).getY() + islandTileSize / 2);
    }

    public void debugStud(){
        for (int i = 0; i < 4; i++) {
            StudentView studentView = new StudentView(i, "student", "blue", StudentView.studentSize);
            studentView.setEnabled();
            int finalI = i;
            studentView.setCallback(mouseEvent -> {
                System.out.println("TEST " + finalI);
                studentView.setDisabled();
            });
            studentPane.add(studentView, i, i);
            StudentContainerPane.setHalignment(studentView, HPos.CENTER);
        }
    }
}
