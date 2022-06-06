package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains most of the game elements, except for those that are specific to players. The islands, clouds,
 * characters, the student bag and the coin heap all go here. All the aforementioned elements except for the islands
 * have their own container.
 */
public class ArchipelagoPane extends AnchorPane {

    /**
     * The controller class for this pane.
     */
    private final GUIController controller;

    /**
     * The HBox that contains the clouds.
     */
    private final CloudContainerPane cloudContainer;

    /**
     * The HBox that contains the student bag and the coin heap.
     */
    private final BagContainerPane bagContainer;

    /**
     * The HBox that contains the characters.
     */
    private final CharContainerPane charContainer;

    /**
     * The length of one side of this pane.
     */
    private final double archipelagoSide = 500.0;

    /**
     * Utility field for roughly calculating the center of this pane.
     */
    private final double centerPos = archipelagoSide / 2.0;

    /**
     * A list of the IDs of the islands in the archipelago. This ID is synchronized with the one present in the server's
     * model.
     */
    private List<Integer> islandsIDs;

    private int islandChosen;
    private int studentChosen;

    /**
     * Constructor for the archipelago. It creates all the elements which are ubiquitous to every match regardless of the
     * number of players or game mode: 12 empty islands and empty containers for characters, clouds and bags.
     * @param controller the controller class that will handle events inside this pane
     */
    public ArchipelagoPane(GUIController controller) {
        this.controller = controller;
        this.setId("archipelagoPane");
        this.setPrefSize(archipelagoSide, archipelagoSide);

        for (int i = 0; i < 12; i++) {
            IslandTilePane newIsland = new IslandTilePane(controller, this, i);
            newIsland.setId("islandTilePane" + i);
            this.getChildren().add(newIsland);
            newIsland.relocate(
                    centerPos + Math.cos(Math.PI/2 - Math.PI / 6 * i)*centerPos,
                    centerPos - Math.sin(Math.PI/2 - Math.PI / 6 * i)*centerPos
            );
        }

        // character container pane goes here
        charContainer = new CharContainerPane(controller);
        this.getChildren().add(charContainer);
        charContainer.relocate(centerPos - 90.0, centerPos + 50.0);

        // cloud hbox goes here
        cloudContainer = new CloudContainerPane(controller);
        this.getChildren().add(cloudContainer);
        cloudContainer.relocate(centerPos - 160.0, centerPos + 360.0);

        // bag goes here
        bagContainer = new BagContainerPane();
        this.getChildren().add(bagContainer);
        bagContainer.relocate(centerPos - 15.0, centerPos - 30.0);
    }

    /**
     * Sets an ID to each IslandTilePane based on the real ID in the server model, and places mother nature on the right
     * island.
     * @param motherNatureIsland the ID of the island that owns mother nature
     * @param islands the IDs of the island tiles in the server model
     */
    public void createIslands(int motherNatureIsland, List<Integer> islands){
        this.islandsIDs = new ArrayList<>();
        //Finishes creating islands
        for (int i = 0; i < 12; i++) {
            IslandTilePane islandTilePane = (IslandTilePane) this.lookup("#islandTilePane" + i);
            islandTilePane.setId("islandTilePane" + islands.get(i));
            this.islandsIDs.add(islands.get(i));
            islandTilePane.createIslandTile(islands.get(i));
        }
        updateMotherNature(motherNatureIsland, islands);
    }

    /**
     * Sets an ID to each CloudPane based on the real ID in the server model.
     * @param numOfPlayers the number of clouds in this game
     * @param cloudIDs the IDs of the clouds in the server model
     */
    public void createClouds(int numOfPlayers,List<Integer> cloudIDs){
        cloudContainer.createClouds(numOfPlayers, cloudIDs);
    }

    /**
     * Sets the elements of the archipelago which are only used in a game in expert mode: three characters with their
     * respective IDs and a coin heap.
     * @param characterIDs the IDs of the characters in the server model
     */
    public void createCharacterAndHeap(List<Integer> characterIDs){

        charContainer.createCharacters(characterIDs);
        bagContainer.createCoinsHeap();
    }

    /**
     * Updates all islands on the location of mother nature.
     * @param motherNatureIsland the ID of the island that owns mother nature
     * @param islands a list of the islands' IDs
     */
    public void updateMotherNature(int motherNatureIsland, List<Integer> islands){
        for(Integer island : islands){
            IslandTilePane islPane =  (IslandTilePane) this.lookup("#islandTilePane" + island);
            islPane.updateMotherNature(motherNatureIsland == island);
        }
    }

    /**
     * Updates all islands on the presence (and color) of a tower on them.
     * @param towers a hashmap with all the islands that have a tower on them and their associated color
     */
    public void updateTowers(HashMap<Integer, TowerColor> towers){
        for(Integer island  : towers.keySet()){
            IslandTilePane islPane =  (IslandTilePane) this.lookup("#islandTilePane" + island);
            islPane.updateTower(towers.get(island));
        }
    }

    /**
     * Updates all islands on the presence (and number) of no-entry tiles on them.
     * @param noEntry a hashmap with all the islands that have a no-entry tile on them and their associated number
     */
    public void updateNoEntry(HashMap<Integer, Integer> noEntry){
        for(Integer islandID : noEntry.keySet()){
            ((IslandTilePane)this.lookup("#islandTilePane" + islandID)).updateNoEntryTile(noEntry.get(islandID));
        }
    }

    /**
     * Updates all islands on the presence, number and color of students on them.
     * @param islandsStuds a hashmap with all the islands that have students and their associated IDs
     * @param studsColor a hashmap with all the students on the islands and their associated color
     */
    public void updateIslandStudents(HashMap<Integer, List<Integer>> islandsStuds, HashMap<Integer, Color> studsColor){
        for(Integer island  : islandsStuds.keySet()){
            IslandTilePane islPane =  (IslandTilePane) this.lookup("#islandTilePane" + island);
            islPane.updateStudents(islandsStuds.get(island), studsColor);
        }
    }

    /**
     * Updates island groups, merging islands on the GUI if they have been merged in the server model.
     * @param islandsConfiguration the current configuration of the islands inside island groups
     */
    public void updateMerge(HashMap<Integer, List<Integer>> islandsConfiguration) {//todo
    }

    /**
     * Updates a single cloud on the presence, number and color of students on it.
     * @param cloud the ID of the cloud to update
     * @param studs a hashmap with each student's ID and color on the cloud
     */
    public void updateCloud(int cloud, HashMap<Integer, Color> studs){
        cloudContainer.updateCloud(cloud, studs);
    }

    /**
     * Updates the students bag with the remaining number of students inside it.
     * @param studentsLeft the remaining number of students inside the bag
     */
    public void updateBag(int studentsLeft){
        bagContainer.updateBag(studentsLeft);
    }

    /**
     * Updates a single character with the given ID on the presence, number and color of students and (in the case of
     * Character 4) the number of no-entry tiles on it.
     * @param ID the character's ID
     * @param newStuds a hashmap with each student's ID and color on the character
     * @param numOfNoEntryTiles the number of no-entry tiles on the character
     */
    public void updateCharacter(int ID, HashMap<Integer, Color> newStuds, int numOfNoEntryTiles, boolean isOvercharged){
        charContainer.updateCharacter(ID, newStuds, numOfNoEntryTiles, isOvercharged);
    }

    /**
     * Updates the coinheap with the remaining number of coins inside it.
     * @param coinsLeft the remaining number of coins in the heap
     */
    public void updateCoinHeap(int coinsLeft){
        bagContainer.updateCoinHeap(coinsLeft);
    }

    public void enableSelectIsland() {
        for (int islandID : islandsIDs) {
            IslandTilePane island = (IslandTilePane) this.lookup("#islandTilePane" + islandID);
            island.setOnMouseClicked(event -> {
                System.out.println("Someone clicked on me, an island tile! " + island.getId());
                setIslandChosen(islandID);
                controller.notifyIsland();
            });
        }
    }

    public void disableSelectIsland() {
        for (int islandID : islandsIDs) {
            IslandTilePane island = (IslandTilePane) this.lookup("#islandTilePane" + islandID);
            island.setOnMouseClicked(event -> {
                System.out.println("I'm disabled! " + island.getId());
            });
        }
    }

    public void setIslandChosen(int islandID) {
        this.islandChosen = islandID;
    }

    public int getIslandChosen() {
        return islandChosen;
    }

    public void enableSelectStudents() {
        for (int islandID : islandsIDs) {
            IslandTilePane island = (IslandTilePane) this.lookup("#islandTilePane" + islandID);
            List<StudentView> students = island.getStudents();
            for (StudentView student : students) {
                student.setCallback(event -> {
                    System.out.println("Student clicked in island! " + student.getId());
                    this.setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                    controller.notifyStudentIsland();
                });
            }
        }
    }

    public void disableSelectStudents() {
        for (int islandID : islandsIDs) {
            IslandTilePane island = (IslandTilePane) this.lookup("#islandTilePane" + islandID);
            List<StudentView> students = island.getStudents();
            for (StudentView student : students) {
                student.setCallback(event -> {
                    System.out.println("I'm disabled! " + student.getId());
                });
            }
        }
    }

    public void setStudentChosen(int studentID) {
        this.studentChosen = studentID;
    }

    public int getStudentChosen() {
        return studentChosen;
    }

    /**
     * Calculates the distance between the merging point of either of the two soon-to-be merged islands and the midpoint
     * of their merging points.
     * @param forwardIndex the index of the island that will move forward to merge
     * @param backIndex the island index of the island that will move backward to merge
     * @return the coordinates representing the distance
     */
    public Point2D calcMergeDiff(int forwardIndex, int backIndex) {
        IslandTilePane backIsland = (IslandTilePane) this.lookup("#islandTilePane" + backIndex);
        Point2D backMergePoint = backIsland.getBackMergePoint();
        IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + forwardIndex);
        Point2D forwardMergePoint = forwardIsland.getForwardMergePoint();
        return backMergePoint.midpoint(forwardMergePoint).subtract(forwardMergePoint);
    }

    /**
     * Animates the forwards motion of the island with the given index, by the amount specified in the given point.
     * @param index the index of the island to animate
     * @param mergeDiff the point that the island will reach in the animation
     */
    public void relocateForward(int index, Point2D mergeDiff) {
        IslandTilePane forwardIsland = (IslandTilePane) this.lookup("#islandTilePane" + index);

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

    /**
     * Animates the backwards motion of the island with the given index, by the amount specified in the given point.
     * @param index the index of the island to animate
     * @param mergeDiff the point that the island will reach in the animation
     */
    public void relocateBack(int index, Point2D mergeDiff) {
        Point2D reverseMergeDiff = new Point2D(- mergeDiff.getX(), - mergeDiff.getY());
        relocateForward(index, reverseMergeDiff);
    }

    /**
     * Now deprecated. Creates the invisible bridges around the island tiles.
     */
    public void createEmptyBridges() {
        double bridgeHeight = 50.0, bridgeLength = 25.0, angleAdjustment = 90.0;
        for (int forwardIndex = 0; forwardIndex < 12; forwardIndex++) {
            int backIndex = forwardIndex!=11 ? forwardIndex+1 : 0;
            Image bridge = new Image("/world/sandstone1.png");
            ImageView bridgeView = new ImageView(bridge);
            bridgeView.setId("bridgeView" + forwardIndex);
            bridgeView.setVisible(false);

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

    /**
     * Now deprecated. Sets the visibility and color of a bridge connecting two islands.
     * @param forwardIndex the index of the island from which the bridge is outgoing
     * @param bridgeColor the color of the bridge
     */
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
        bridgeView.setVisible(true);

        DoubleProperty opacity = bridgeView.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

    /**
     * Debug function that displays some sample students on each island tile, cloud and character.
     */
    public void debugStud() {
        for (int i = 0; i < 12; i++) {
            IslandTilePane islandTilePane = (IslandTilePane) this.lookup("#islandTilePane" + i);
            islandTilePane.debugStud();
        }
        cloudContainer.debugStud();
        charContainer.debugStud();
    }

}
