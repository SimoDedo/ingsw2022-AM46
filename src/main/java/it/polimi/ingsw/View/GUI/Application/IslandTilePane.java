package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class models an island tile. The archipelago has a number of these tiles and can iterate through them and move them
 * around as a whole. A tile actually contains the background image, a pane for island modifiers (pawns that apply an effect
 * on the tile) and a student pane.
 */
public class IslandTilePane extends StackPane {

    /**
     * Boolean that activates/deactivates the party mode. When in party mode, island tiles have one additional skin and
     * are randomly rotated.
     */
    public static final boolean partyMode = false;

    /**
     * The index of this tile inside the archipelago.
     */
    final int index;

    /**
     * The height and width of the tile.
     */
    static final double islandTileSize = 120.0;

    /**
     * A constant that defines the position of the merge points relative to the true edges of the image of the tile. A
     * lower constant means the tiles get closer when merging. A constant of 1 means the merge points are at the furthest
     * distance from the center (i.e. they're the edges of a hexagon inscribed inside the tile's square).
     */
    static final double shrinkConstant = 0.85;

    /**
     * The color of the tower on this tile (null if there is none).
     */
    private TowerColor towerColor;

    /**
     * The merge point used for the merging animation when this tile merges "forwards", i.e. with the tile that is in front
     * of it clockwise.
     */
    private Point2D forwardMergePoint;

    /**
     * The merge point used for the merging animation when this tile merges "backwards", i.e. with the tile that is behind
     * it clockwise.
     */
    private Point2D backMergePoint;

    /**
     * The pane containing island modifiers: mother nature, the tower and the no-entry tiles.
     */
    private final GridPane islandModifiersPane;

    /**
     * The ImageView of mother nature, to be placed inside the island modifiers pane.
     */
    private final ImageView motherNature;

    /**
     * The ImageView of a no-entry tile, to be placed inside the island modifiers pane.
     */
    private final ImageView noEntryTile;

    /**
     * The number of no-entry tiles on this island.
     */
    private final Text noEntryTileText;

    /**
     * The box that contains the island modifiers pane and the students' pane.
     */
    private final VBox gridContainer;

    /**
     * The pane containing the students on this island.
     */
    private StudentContainerPane studentPane;

    /**
     * A list containing the "coordinates" (column and row) of the free spots on the student pane.
     */
    private List<Pair<Integer, Integer>> freeStudSpots;

    /**
     * The size of a student in the student pane. It is reduced when the student pane is adapted to host more students.
     */
    private double currentStudSize;

    /**
     * The current number of rows and columns of the student pane.
     */
    private int currentStudGridSize;

    /**
     * Creates the island background image (has a different outcome if partyMode is set to true), and creates the (initially
     * empty) modifier pane.
     * @param index the index of this tile inside the archipelago
     */
    public IslandTilePane(int index) {
        this.index = index;
        currentStudSize = StudentView.studentSize;
        currentStudGridSize = 4;

        Image islandTileBackground = new Image("/world/islandtile"
                + (partyMode ? ThreadLocalRandom.current().nextInt(1, 5) : (index%3+1))
                + ".png", 300, 300, true, true);

        ImageView imageView = new ImageView(islandTileBackground);
        imageView.setId("islandView");
        imageView.setEffect(Effects.disabledIslandShadow);
        imageView.setRotate(60 * (partyMode ? ThreadLocalRandom.current().nextInt(6) : 0));
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
    }

    /**
     * Creates the student pane on this tile with its associated list of free spots.
     * @param ID the StudentContainer ID of the student pane
     */
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

    /**
     * Updates the color of the tower placed on this tile.
     * @param towerColor the updated tower color
     */
    public void updateTower(TowerColor towerColor){
        if (towerColor != null && towerColor != this.towerColor){
            islandModifiersPane.getChildren().remove(islandModifiersPane.lookup("#tower0"));
            islandModifiersPane.add(new PawnView(0, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize), 0, 0);
            this.towerColor = towerColor;
        }
    }

    /**
     * Updates the presence of mother nature on this tile. Simply sets the mother nature pawn to invisible if it is not
     * present.
     * @param isMotherNatureHere true if mother nature sits on this tile, false otherwise
     */
    public void updateMotherNature(boolean isMotherNatureHere){
        motherNature.setVisible(isMotherNatureHere);
    }

    /**
     * Updates the presence and number of no-entry tiles on this island tile. If there are no no-entry tiles, the associated icon
     * is simply set to invisible.
     * @param numOfNoEntryTiles the number of no-entry tiles on this island tile
     */
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

    /**
     * Updates the number and position of students on this tile. Note that this method only adds students to islands because
     * in the current implementation of this game students are never removed from them.
     * @param newStudents a list of student IDs
     * @param studColor a hash map of the colors associated with each student ID
     */
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

    /**
     * Resizes the student grid to accomodate for a larger number of students when the student pane of this island runs
     * out of free spots. Note that this method only shrinks the student pane and its students, but it does not enlarge
     * it, because students cannot be removed from islands in the current implementation of the game.
     */
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

    /**
     * Returns a list of the ImageViews of each student present on this island.
     * @return a list of student ImageViews that are present on this island
     */
    public List<StudentView> getStudents() {
        List<StudentView> retval = new ArrayList<>();
        for (Node node : studentPane.getChildren()) {
            retval.add((StudentView) node);
        }
        return retval;
    }

    /**
     * Calculates and returns the forward merge point of this island tile.
     * @return the forward merge point of this tile
     */
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

    /**
     * Calculates and returns the backwards merge point of this island tile.
     * @return the backwards merge point of this tile
     */
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

    /**
     * Calculates and returns the center of this island tile.
     * @return the center point of this tile
     */
    public Point2D getCenter() {
        return new Point2D(localToParent(0,0).getX() + islandTileSize / 2, localToParent(0,0).getY() + islandTileSize / 2);
    }

}
