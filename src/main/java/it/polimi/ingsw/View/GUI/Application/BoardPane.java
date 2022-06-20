package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.View.GUI.ObservableGUI;
import it.polimi.ingsw.View.GUI.ObserverGUI;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.util.*;

/**
 * This class represents the player's board. It contains the entrance, the dining room with the professor space, and the
 * tower space,
 */
public class BoardPane extends StackPane implements ObservableGUI {

    /**
     * An integer representing the position of the user in the players' list.
     */
    private final int nickID;

    /**
     * The height of the player's board.
     */
    private final double boardHeight;

    /**
     * The width of the player's board.
     */
    private final double boardWidth;

    /**
     * The percentage of width that the entrance occupies compared to the whole board.
     */
    private final double entrancePct = (500.0/3304.0) * 100;

    /**
     * The percentage of width that the dining room occupies compared to the whole board.
     */
    private final double diningRoomPct = ((2270.0 - 500.0) /3304.0) * 100;

    /**
     * The percentage of width that the professor space occupies compared to the whole board.
     */
    private final double professorPct = ((2560.0 - 2270.0) / 3304.0) * 100;

    /**
     * The percentage of width that the tower space occupies compared to the whole board.
     */
    private final double towerPct = ((3304.0 - 2560.0) / 3304.0) * 100;


    /**
     * The grid on which to place the entrance, dining room and tower space.
     */
    private final GridPane mainGrid;

    /**
     * The entrance space.
     */
    private StudentContainerPane entrance;

    /**
     * A list containing the "coordinates" (column and row) of the free spots in the entrance space.
     */
    private List<Pair<Integer, Integer> > freeEntranceSpots;

    /**
     * The dining room space, with the four tables but without the professors table.
     */
    private GridPane diningRoom;

    /**
     * A map representing the color associated with each table.
     */
    private HashMap<Color, StudentContainerPane> tables;

    /**
     * A map containing the shadow effect on the tables.
     */
    private HashMap<Color, StackPane> shadowedTablePanes;

    /**
     * A map representing the order in which the tables are displayed, from top to bottom.
     */
    private final HashMap<Color, Integer> tableOrder;

    /**
     * A list containing the "coordinates" (column and row) of the free spots in the dining room.
     */
    private List<Pair<Integer, Integer>> freeDRSpots;

    /**
     * The professors' table space.
     */
    private GridPane professors;

    /**
     * The tower space.
     */
    private GridPane towerSpace;

    /**
     * The color associated with this player board (and its owner/its owner's team).
     */
    private TowerColor towerColor;

    /**
     * The number of towers on this board.
     */
    private int numOfTowers;

    /**
     * Comparator function for determining the first available spot in a free spots list. Between two spots, it will favor
     * the one that is closer to the top. If they are both on the same row it will favor the one that is closer to the left.
     * Comparing all spots in a space thus means that they should be filled from left to right and from top to bottom.
     */
    private final Comparator<Pair<Integer, Integer>> compareGridSpot = (p, p2) -> {
        if(p.getValue() < p2.getValue()) return -1;
        else if (p.getValue() > p2.getValue()) return 1;
        else {
            if (p.getKey() < p2.getKey()) return  -1;
            else if (p.getKey() > p2.getKey()) return 1;
            else return 0;
        }
    };

    /**
     * The observer of this GUI element.
     */
    private ObserverGUI observer;

    /**
     * The table chosen by the user through clicking.
     */
    private int tableChosen;

    /**
     * The student chosen by the user through clicking.
     */
    private int studentChosen;

    /**
     * Constructor for the class. It creates the grid with the right spacing between elements, and the image of the board
     * underneath.
     * @param nickID the index of the PlayerPane that has this BoardPane
     * @param boardHeight the height of the board
     */
    public BoardPane(int nickID, double boardHeight) {
        this.nickID = nickID;
        this.boardHeight = boardHeight;
        this.boardWidth = boardHeight * (3304.0 / 1413.0);

        this.setId("boardPane" + nickID);
        Image playerBoard = new Image("/world/board_roundedcorners.png", 600, 600,true, false);
        ImageView imageViewPB = new ImageView(playerBoard);
        imageViewPB.setPreserveRatio(true);
        imageViewPB.setFitHeight(boardHeight);
        imageViewPB.setSmooth(true);
        imageViewPB.setCache(true);
        imageViewPB.setEffect(new DropShadow());
        this.getChildren().add(imageViewPB);

        mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER_LEFT);
        //Create main grid so that it has 5 columns and 1 row of fixed percentage
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(entrancePct * 100);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(diningRoomPct * 100);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(professorPct * 100);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(towerPct * 100);
        mainGrid.getColumnConstraints().addAll(c0, c1, c2, c3);
        RowConstraints r = new RowConstraints();
        r.setPercentHeight(100);
        mainGrid.getRowConstraints().addAll(r);

        tableOrder = new HashMap<>(Map.of(Color.GREEN, 0, Color.RED, 1, Color.YELLOW, 2, Color.PINK, 3, Color.BLUE, 4));

        this.getChildren().add(mainGrid);
    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
    }

    /**
     * Enables the selection of tables.
     */
    public void enableSelectTables() {
        for (Map.Entry<Color, StudentContainerPane> entry : tables.entrySet()) {
            entry.getValue().setOnMouseClicked(event -> {
                setTableChosen(Integer.parseInt(entry.getValue().getId().substring("tablePane".length())));
                observer.notifyTable();
            });
        }
    }

    /**
     * Enables the selection of a specific table of the given color.
     * @param color the color of the table to make selectable
     */
    public void enableSelectTables(Color color) {
        shadowedTablePanes.get(color).setEffect(Effects.enabledTableEffect);
        shadowedTablePanes.get(color).setStyle("-fx-background-color: rgba(113,215,178,0.16); " +
                "-fx-background-insets: 0; " +
                "-fx-background-radius: 0; ");
        tables.get(color).setOnMouseEntered(e -> {
            shadowedTablePanes.get(color).setStyle("-fx-background-color: rgba(113,215,178,0.27); " +
                    "-fx-background-insets: 0; " +
                    "-fx-background-radius: 0; ");
            shadowedTablePanes.get(color).setEffect(Effects.hoveringTableEffect);
        });
        tables.get(color).setOnMouseExited(e -> {
            shadowedTablePanes.get(color).setStyle("-fx-background-color: rgba(113,215,178,0.16); " +
                    "-fx-background-insets: 0; " +
                    "-fx-background-radius: 0; ");
            shadowedTablePanes.get(color).setEffect(Effects.enabledTableEffect);
        });
        tables.get(color).setOnMouseClicked(event -> {
            setTableChosen(Integer.parseInt(tables.get(color).getId().substring("tablePane".length())));
            observer.notifyTable();
        });
    }

    /**
     * Disables the selection of all tables.
     */
    public void disableSelectTables() {
        for (StudentContainerPane table : tables.values()) {
            table.setOnMouseExited(null);
            table.setOnMouseEntered(null);
            table.setOnMouseClicked(event -> {
            });
        }
        for (StackPane shadowedPane : shadowedTablePanes.values()) {
            shadowedPane.setEffect(Effects.disabledTableEffect);
            shadowedPane.setStyle(null);
        }
    }

    /**
     * Setter for the table chosen.
     * @param tableID the ID of the chosen table
     */
    public void setTableChosen(int tableID) {
        this.tableChosen = tableID;
    }

    /**
     * Getter for the table chosen.
     * @return the ID of the chosen table
     */
    public int getTableChosen() {
        return tableChosen;
    }

    /**
     * Enables the selection of students in the entrance space.
     */
    public void enableSelectStudentsEntrance() {
        List<StudentView> entranceStudents = entrance.getStudents();
        for (StudentView student : entranceStudents) {
            student.setEnabled();
            student.setCallback(event -> {
                this.setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                observer.notifyStudentEntrance();
            });
        }
    }

    /**
     * Disables the selection of students in the entrance space.
     */
    public void disableSelectStudentsEntrance() {
        List<StudentView> entranceStudents = entrance.getStudents();
        for (StudentView student : entranceStudents) {
            student.setDisabled();
            student.setCallback(event -> {
            });
        }
    }

    /**
     * Enables the selection of students in the dining room space.
     */
    public void enableSelectStudentsDR() {
        List<StudentView> DRStudents = new ArrayList<>();
        for (StudentContainerPane table : tables.values()) {
            DRStudents.addAll(table.getStudents());
        }
        for (StudentView student : DRStudents) {
            student.setEnabled();
            student.setCallback(event -> {
                this.setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                observer.notifyStudentDR();
            });
        }
    }

    /**
     * Disables the selection of students in the dining room space.
     */
    public void disableSelectStudentsDR() {
        List<StudentView> DRStudents = new ArrayList<>();
        for (StudentContainerPane table : tables.values()) {
            DRStudents.addAll(table.getStudents());
        }
        for (StudentView student : DRStudents) {
            student.setDisabled();
            student.setCallback(event -> {
            });
        }
    }

    /**
     * Setter for the student chosen.
     * @param studentID the ID of the chosen student
     */
    public void setStudentChosen(int studentID) {
        this.studentChosen = studentID;
    }

    /**
     * Getter for the student chosen.
     * @return the ID of the chosen student
     */
    public int getStudentChosen() {
        return studentChosen;
    }

    /**
     * Utility function for creating a GridPane with the given dimensions and size constraints.
     * @param toCreate the GridPane to set up
     * @param widthPct the percentage of the board's width that this GridPane will occupy
     * @param rows the number of rows
     * @param columns the number of columns
     * @param paddingHPct the left and right padding, as percentage of the board's width
     * @param paddingVPct the top and bottom padding, as percentage of the board's height
     * @param paddingExtraRightPct an additional extra padding on the right, needed for certain grids
     */
    private void createGrid(GridPane toCreate, double widthPct, int rows, int columns, double paddingHPct,
                            double paddingVPct, double paddingExtraRightPct){
        toCreate.setAlignment(Pos.CENTER);
        for (int i = 0; i < rows; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(100.0 / rows);
            toCreate.getRowConstraints().add(r);
        }
        for (int i = 0; i < columns; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(100.0 / columns);
            toCreate.getColumnConstraints().add(c);
        }
        toCreate.setPadding(new Insets(boardHeight * (paddingVPct/100), boardWidth * (paddingHPct/100 + paddingExtraRightPct/100) * (widthPct/100),
                boardHeight * (paddingVPct/100), boardWidth * (paddingHPct/100) * (widthPct/100)));

    }

    /**
     * Creates the entrance space with the given StudentContainerPane ID.
     * @param entranceID the ID of the entrance space
     */
    public void createEntrance(int entranceID){
        entrance = new StudentContainerPane("entrancePane", entranceID,
                boardWidth, boardHeight, entrancePct, 5, 2, 10.0, 5.0, 15.0);
        mainGrid.add(entrance, 0 , 0);
        freeEntranceSpots = new ArrayList<>();
        for(int i = 0 ; i < 10 ; i++){
            freeEntranceSpots.add(new Pair<>(i % 2, i / 2));
        }
    }

    /**
     * Creates the dining room space with the given StudentContainerPane IDs.
     * @param tableIDs a hashmap of the StudentContainerPane ID associated with each table color
     */
    public void createDiningRoom(HashMap<Color, Integer> tableIDs){
        diningRoom = new GridPane();
        createGrid(diningRoom, diningRoomPct, 5, 1, 5.0, 7.7, 0.0);
        diningRoom.setId("diningRoomPane" + nickID);

        tables = new HashMap<>();
        shadowedTablePanes = new HashMap<>();

        for (Color color : Color.values()){
            StackPane shadowedPane = new StackPane();
            shadowedPane.setEffect(Effects.disabledTableEffect);
            shadowedTablePanes.put(color, shadowedPane);
            StudentContainerPane table = new StudentContainerPane("tablePane", tableIDs.get(color),
                    boardWidth, boardHeight, diningRoomPct, 1, 10, 0, 0, 0);
            table.setPickOnBounds(true);
            tables.put(color, table);
            diningRoom.add(shadowedPane, 0, tableOrder.get(color));

            shadowedPane.getChildren().add(table);
            //diningRoom.add(table, 0 , tableOrder.get(color)); //alternative to above, this way no effect is applied to student on table
        }
        mainGrid.add(diningRoom, 1, 0);

        freeDRSpots = new ArrayList<>();
        for (int row = 0; row < 5; row++) {
            for(int col = 0 ; col < 10 ; col++){
                freeDRSpots.add(new Pair<>(col, row));
            }
        }
        freeDRSpots.sort(compareGridSpot);
    }

    /**
     * Creates the professors space.
     */
    public void createProfessors(){
        professors = new GridPane();
        professors.setId("professorPane" + nickID);
        createGrid(professors, professorPct, 5, 1, 6.0, 7.7, 0.0);
        mainGrid.add(professors,2,0);
    }

    /**
     * Creates the tower space.
     */
    public void createTowerSpace(TowerColor towerColor) {
        this.towerColor = towerColor;
        towerSpace = new GridPane();
        towerSpace.setId("towerSpacePane" + nickID);
        createGrid(towerSpace, towerPct, 4, 2, 12, 10, 0);
        mainGrid.add(towerSpace, 3, 0);
    }

    /**
     * Updates the entrance space with the updated students. It is equivalent to calling removeOldStudentsFromEntrance and
     * addNewStudentsToEntrance
     * @param students a hashmap with the student IDs and their respective color
     */
    public void updateEntrance(HashMap<Integer, Color> students){
        removeOldStudentsFromEntrance(students);
        addNewStudentsToEntrance(students);
    }

    /**
     * Method for removing from the entrance those students that are not present in the given updated list of students.
     * @param students a hashmap with the student IDs and their respective color
     */
    private  void removeOldStudentsFromEntrance(HashMap<Integer, Color> students){
        List<Node> studsBefore = new ArrayList<>(entrance.getChildren());
        List<String> studsNow = students.keySet().stream().map(id -> "student" + id).toList();
        for(Node studBefore : studsBefore){
            if(studBefore instanceof StudentView){
                if(! studsNow.contains(studBefore.getId())){
                    Pair<Integer, Integer> spotToFree = new Pair<>(GridPane.getColumnIndex(studBefore), GridPane.getRowIndex(studBefore));
                    entrance.getChildren().remove(studBefore);
                    freeEntranceSpots.add(spotToFree);
                }
            }
        }
        freeEntranceSpots.sort(compareGridSpot);
    }

    /**
     * Method for adding to the entrance those students that weren't present before, but are present in the updated list
     * of students.
     * @param students a hashmap with the student IDs and their respective color
     */
    private void addNewStudentsToEntrance(HashMap<Integer, Color> students){
        List<Node> studsBefore = entrance.getChildren();
        List<String> studsBeforeIDs = studsBefore.stream().map(Node::getId).toList();
        for (Map.Entry<Integer, Color> stud : students.entrySet()){
            if(! studsBeforeIDs.contains("student" + stud.getKey())){
                Pair<Integer, Integer> freeSpot = freeEntranceSpots.get(0);
                StudentView studToAdd = new StudentView(stud.getKey(), stud.getValue().toString().toLowerCase(),StudentView.studentSize);
                entrance.add(studToAdd, freeSpot.getKey(), freeSpot.getValue());
                freeEntranceSpots.remove(freeSpot);
                GridPane.setHalignment(studToAdd, HPos.CENTER);
            }
        }
    }

    /**
     * Updates the table of the given color with an updated list of students sitting on it. It is equivalent to calling
     * removeOldStudentsFromTable and addNewStudentsToTable.
     * @param tableColor the color of the table to update
     * @param students a list of the student IDs on that table
     */
    public void updateTable(Color tableColor, List<Integer> students){
        removeOldStudentsFromTable(tableColor, students);
        addNewStudentsToTable(tableColor, students);
    }

    /**
     * Method for removing from the table of the given color those students that are not present in the given updated list
     * of students.
     * @param tableColor the color of the table to update
     * @param students a list of the student IDs on that table
     */
    private  void removeOldStudentsFromTable(Color tableColor, List<Integer> students){
        List<Node> studsBefore = new ArrayList<>(tables.get(tableColor).getChildren());
        List<String> studsNow = students.stream().map(id -> "student" + id).toList();
        for(Node studBefore : studsBefore){
            if(studBefore instanceof StudentView){
                if(! studsNow.contains(studBefore.getId())){
                    Pair<Integer, Integer> spotToFree = new Pair<>(GridPane.getColumnIndex(studBefore), GridPane.getColumnIndex(studBefore));
                    tables.get(tableColor).getChildren().remove(studBefore);
                    freeDRSpots.add(new Pair<>(spotToFree.getKey(), tableOrder.get(tableColor)));
                }
            }
        }
        freeDRSpots.sort(compareGridSpot);
    }

    /**
     * Method for adding to the table of the given color those students that weren't present before, but are in the updated
     * list of students.
     * @param tableColor the color of the table to update
     * @param students a list of the student IDs on that table
     */
    private void addNewStudentsToTable(Color tableColor, List<Integer> students){
        List<Pair<Integer,Integer>> freeTableSpots =
                new ArrayList<>(freeDRSpots.stream().filter(spot -> spot.getValue().equals(tableOrder.get(tableColor))).toList());
        List<Node> studsBefore = tables.get(tableColor).getChildren();
        List<String> studsBeforeIDs = studsBefore.stream().map(Node::getId).toList();
        for (Integer stud : students){
            if(! studsBeforeIDs.contains("student" + stud)){
                Pair<Integer, Integer> freeSpot = freeTableSpots.get(0);
                StudentView studToAdd = new StudentView(stud, tableColor.toString().toLowerCase(),StudentView.studentSize);
                tables.get(tableColor).add(studToAdd, freeSpot.getKey(), 0);
                freeTableSpots.remove(freeSpot);
                freeDRSpots.remove(freeSpot);
                GridPane.setHalignment(studToAdd, HPos.CENTER);
            }
        }
    }

    /**
     * Updates the professor space with an updated list of the owned professors.
     * @param newProfessorsOwned a list of the colors of the professors present on this board
     */
    public void updateProfessors(List<Color> newProfessorsOwned){
        professors.getChildren().clear();
        for(Color profColor : newProfessorsOwned){
            PawnView prof = new PawnView(0, "professor", profColor.toString().toLowerCase(), StudentView.studentSize);
            professors.add(prof, 0, tableOrder.get(profColor));
            GridPane.setHalignment(prof, HPos.CENTER);
        }
    }

    /**
     * Updates the tower space with the new number of towers owned.
     * @param newNumOfTowers the number of towers present on this board
     */
    public void updateTowers(int newNumOfTowers){
        if(newNumOfTowers < numOfTowers){
            towerSpace.getChildren().clear();
            for (int i = 0; i < newNumOfTowers ; i++) {
                PawnView tower = new PawnView(-1, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize);
                GridPane.setHalignment(tower, HPos.CENTER);
                towerSpace.add(tower, i%2, i/2);
            }
        }
        else if(newNumOfTowers > numOfTowers){
            for (int i = numOfTowers; i < newNumOfTowers ; i++) {
                PawnView tower = new PawnView(-1, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize);
                towerSpace.add(tower, i%2, i/2);
                GridPane.setHalignment(tower, HPos.CENTER);
            }
        }
        this.numOfTowers = newNumOfTowers;
    }

}
