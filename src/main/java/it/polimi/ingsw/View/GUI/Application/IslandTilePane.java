package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class IslandTilePane extends StackPane {

    boolean partyMode = false;
    int index;

    static double islandTileSize = 120.0, shrinkConstant = 0.80, modifierSize = 25.0, studentSize = 15.0;

    private TowerColor towerColor;

    private Point2D forwardMergePoint, backMergePoint;

    private final GridPane islandModifiersPane;
    private final ImageView motherNature;
    private final ImageView noEntryTile;
    private final Text noEntryTileText;
    private final VBox gridContainer;
    private StudentContainerPane studentPane;
    private List<Pair<Integer, Integer>> freeStudSpots;
    private double currentStudSize;
    private int currentStudGridSize;

    public IslandTilePane(GUIController controller, ArchipelagoPane archipelago, int index) {
        this.index = index;
        currentStudSize = StudentView.studentSize;
        currentStudGridSize = 4;

        Image islandTileBackground = new Image("/world/islandtile" + ThreadLocalRandom.current().nextInt(1, partyMode ? 5 : 4) + ".png",
        300, 300, true, true);
        ImageView imageView = new ImageView(islandTileBackground);
        imageView.setId("islandView");
        imageView.setEffect(Effects.disabledIslandShadow);
        imageView.setRotate(60 * ThreadLocalRandom.current().nextInt(partyMode ? 6 : 1));
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

        //Preload mothernature and noentry images
        motherNature = new PawnView(0, "mothernature", "", PawnView.pawnSize);
        motherNature.setVisible(false);
        islandModifiersPane.add(motherNature, 1, 0);

        Image noEntry = new Image("/pawns/noentrytile.png", 50, 50, true, true);
        noEntryTile = new ImageView(noEntry);
        noEntryTile.setEffect(new DropShadow());
        noEntryTile.setPreserveRatio(true);
        noEntryTile.setFitHeight(PawnView.pawnSize);
        noEntryTile.setVisible(false);
        islandModifiersPane.add(noEntryTile, 2,0);
        GridPane.setHalignment(noEntryTile, HPos.CENTER);
        noEntryTileText = new Text();
        noEntryTileText.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 20));
        noEntryTileText.setVisible(false);
        noEntryTileText.setFill(javafx.scene.paint.Color.WHITE);
        noEntryTileText.setEffect(new DropShadow());
        noEntryTileText.setStyle("-fx-stroke: black;");
        noEntryTileText.setStyle("-fx-stroke-width: 3;");
        islandModifiersPane.add(noEntryTileText, 2, 0);
        GridPane.setHalignment(noEntryTileText, HPos.CENTER);

        this.setPickOnBounds(false);

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
                islandTileSize, islandTileSize *0.8, 100, currentStudGridSize, currentStudGridSize,
                25.0, 0.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        gridContainer.getChildren().add(studentPane);
        freeStudSpots = new ArrayList<>();
        for (int i = 0; i < currentStudGridSize; i++) {
            for (int j = 0; j < currentStudGridSize; j++) {
                freeStudSpots.add(new Pair<>(i, j));
            }
        }
    }

    public void updateTower(TowerColor towerColor){
        if (towerColor != null && towerColor != this.towerColor){
            islandModifiersPane.getChildren().remove(islandModifiersPane.lookup("#tower0"));
            islandModifiersPane.add(new PawnView(0, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize), 0, 0);
            this.towerColor = towerColor;
        }
    }

    public void updateMotherNature(boolean isMotherNatureHere){
        motherNature.setVisible(isMotherNatureHere);
    }

    public void removeMotherNature(){
        motherNature.setVisible(true);
    }

    public void updateNoEntryTile(int numOfNoEntryTiles){
        if(numOfNoEntryTiles > 0){
            noEntryTile.setVisible(true);
            noEntryTileText.setText(String.valueOf(numOfNoEntryTiles));
            noEntryTileText.setVisible(true);
        }
        else {
            noEntryTile.setVisible(false);
            noEntryTileText.setVisible(false);
        }
    }

    public void updateStudents(List<Integer> newStudents,HashMap<Integer, Color> studColor){
        //This only adds, remove function could be implemented but students are NEVER removed from islands in a real game
        List<Node> studsBefore = studentPane.getChildren();
        List<String> studsBeforeID = studsBefore.stream().map(Node::getId).toList();
        for (Integer stud : newStudents){
            if(! studsBeforeID.contains("student" + stud)){
                int rand = new Random().nextInt(freeStudSpots.size());
                studentPane.add(new StudentView(stud, "student", studColor.get(stud).toString().toLowerCase(), currentStudSize),
                        freeStudSpots.get(rand).getKey(), freeStudSpots.get(rand).getValue());
                freeStudSpots.remove(rand);
                if(freeStudSpots.size() == 0){
                    resizeStudGrid();
                }
            }
        }
    }

    private void resizeStudGrid(){
        currentStudGridSize++;
        currentStudSize = currentStudSize * 7/8;
        int ID = Integer.parseInt(studentPane.getId().substring("islandStudentsPane".length()));
        StudentContainerPane newStudGrid = new StudentContainerPane("islandStudentsPane", ID,
                islandTileSize, islandTileSize *0.8, 100, currentStudGridSize, currentStudGridSize,
                25.0, 0.0, 0.0);

        List<Node> studsBefore = new ArrayList<>(studentPane.getChildren());
        for(Node stud : studsBefore){
            if(stud instanceof StudentView){
                Pair<Integer, Integer> spot = new Pair<>(GridPane.getColumnIndex(stud), GridPane.getRowIndex(stud));
                studentPane.getChildren().remove(stud);
                newStudGrid.add(
                        new StudentView(Integer.parseInt(stud.getId().substring("student".length())) ,"student",
                                ((StudentView) stud).getColor(),currentStudSize),
                        spot.getKey(), spot.getValue()
                );
            }
        }
        for (int i = 0; i < currentStudGridSize; i++) {
            freeStudSpots.add(new Pair<>(i, currentStudGridSize - 1));
            if(i != currentStudGridSize - 1)
                freeStudSpots.add(new Pair<>(currentStudGridSize - 1, i));
        }

        gridContainer.getChildren().remove(studentPane);
        gridContainer.getChildren().add(newStudGrid);
        studentPane = newStudGrid;
    }

    public List<StudentView> getStudents() {
        List<StudentView> retval = new ArrayList<>();
        for (Node node : studentPane.getChildren()) {
            retval.add((StudentView) node);
        }
        return retval;
    }

    public Point2D getForwardMergePoint() {
        int side;
        switch (index) {
            case 0, 1 -> side = 2;
            case 2, 3 -> side = 3;
            case 4, 5 -> side = 4;
            case 6, 7 -> side = 5;
            case 8, 9 -> side = 0;
            default -> side = 1;
        }
        Point2D mergePointDiff = new Point2D(
                Math.cos(Math.PI/2 - Math.PI/3*side)* islandTileSize / 2.0 * shrinkConstant,
                - Math.sin(Math.PI/2 - Math.PI/3*side)* islandTileSize/ 2.0 * shrinkConstant
        );
        forwardMergePoint = getCenter().add(mergePointDiff);
        return forwardMergePoint;
    }

    public Point2D getBackMergePoint() {
        int side;
        switch (index) {
            case 1, 2 -> side = 5;
            case 3, 4 -> side = 0;
            case 5, 6 -> side = 1;
            case 7, 8 -> side = 2;
            case 9, 10 -> side = 3;
            default -> side = 4;
        }
        Point2D mergePointDiff = new Point2D(
                Math.cos(Math.PI/2 - Math.PI/3*side)* islandTileSize / 2.0 * shrinkConstant,
                - Math.sin(Math.PI/2 - Math.PI/3*side)* islandTileSize/ 2.0 * shrinkConstant
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
