package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import it.polimi.ingsw.Utils.Enum.Color;
import javafx.util.Pair;

import java.util.*;

public class BoardPane extends StackPane {

    private final String nickname;

    private double boardHeight;
    private double boardWidth;
    private final double entrancePct = (500.0/3304.0) * 100;
    private final double diningRoomPct = ((2270.0 - 500.0) /3304.0) * 100;
    private final double professorPct = ((2560.0 - 2270.0) / 3304.0) * 100;
    private final double towerPct = ((3304.0 - 2560.0) / 3304.0) * 100;

    private final GridPane mainGrid;

    private StudentContainerPane entrance;
    private List<Pair<Integer, Integer> > freeEntranceSpots;

    private GridPane diningRoom;
    private HashMap<Color, StudentContainerPane> tables;

    private final HashMap<Color, Integer> tableOrder;

    private List<Pair<Integer, Integer>> freeDRSpots;

    private GridPane professors;

    private GridPane towerSpace;

    private TowerColor towerColor;

    private int numOfTowers;

    private final Comparator<Pair<Integer, Integer>> compareGridSpot = (p, p2) -> {
        if(p.getValue() < p2.getValue()) return -1;
        else if (p.getValue() > p2.getValue()) return 1;
        else {
            if (p.getKey() < p2.getKey()) return  -1;
            else if (p.getKey() > p2.getKey()) return 1;
            else return 0;
        }
    };
    private GUIController controller;

    private int tableChosen;

    public BoardPane(GUIController controller, String nickname, double boardHeight) {
        this.controller = controller;
        this.nickname = nickname;
        this.boardHeight = boardHeight;
        this.boardWidth = boardHeight * (3304.0 / 1413.0);

        this.setId("boardPane" + nickname);
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

    public void enableSelectDR() {
        diningRoom.setOnMouseClicked(event -> {
            System.out.println("Someone clicked on the dining room! " + diningRoom.getId());
            controller.notifyDR();
        });
    }

    public void disableSelectDR() {
        diningRoom.setOnMouseClicked(event -> {
            System.out.println("I'm disabled!");
        });
    }

    public void enableSelectTables() {
        disableSelectDR();
        for (Map.Entry<Color, StudentContainerPane> entry : tables.entrySet()) {
            entry.getValue().setOnMouseClicked(event -> {
                System.out.println("Someone clicked on a table! " + entry.getValue().getId());
                setTableChosen(tableOrder.get(entry.getKey())); //FIXME has to use actual ids and not their positional id
                controller.notifyTableChar();
            });
        }
    }

    public void disableSelectTables() {
        enableSelectDR();
        for (StudentContainerPane table : tables.values()) {
            table.setOnMouseClicked(event -> {
                System.out.println("I'm a disabled table! " + table.getId());
            });
        }
    }

    public void setTableChosen(int tableID) {
        this.tableChosen = tableID;
    }

    public int getTableChosen() {
        return tableChosen;
    }

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

    public void createEntrance(int entranceID){
        entrance = new StudentContainerPane("entrancePane", entranceID,
                boardWidth, boardHeight, entrancePct, 5, 2, 10.0, 5.0, 15.0);
        mainGrid.add(entrance, 0 , 0);
        freeEntranceSpots = new ArrayList<>();
        for(int i = 0 ; i < 10 ; i++){
            freeEntranceSpots.add(new Pair<>(i % 2, i / 2));
        }
    }

    public void createDiningRoom(HashMap<Color, Integer> tableIDs){
        diningRoom = new GridPane();
        createGrid(diningRoom, diningRoomPct, 5, 1, 5.0, 7.7, 0.0);
        diningRoom.setId("diningRoomPane" + nickname);

        tables = new HashMap<>();

        for (Color color : Color.values()){
            StudentContainerPane table = new StudentContainerPane("tablePane", tableIDs.get(color),
                    boardWidth, boardHeight, diningRoomPct, 1, 10, 0, 0, 0);
            tables.put(color, table);
            diningRoom.add(table, 0, tableOrder.get(color));
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

    public void createProfessors(){
        professors = new GridPane();
        professors.setId("professorPane" + nickname);
        createGrid(professors, professorPct, 5, 1, 6.0, 7.7, 0.0);
        mainGrid.add(professors,2,0);
    }

    public void createTowerSpace(TowerColor towerColor, int number){
        this.towerColor = towerColor;
        towerSpace = new GridPane();
        towerSpace.setId("towerSpacePane" + nickname);
        createGrid(towerSpace, towerPct, 4, 2, 12, 10, 0);
        mainGrid.add(towerSpace, 3, 0);
    }

    public void updateEntrance(HashMap<Integer, Color> students){
        removeOldStudentFromEntrance(students);
        addNewStudentsToEntrance(students);
    }

    private  void removeOldStudentFromEntrance(HashMap<Integer, Color> students){
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

    private void addNewStudentsToEntrance(HashMap<Integer, Color> students){
        List<Node> studsBefore = entrance.getChildren();
        List<String> studsBeforeIDs = studsBefore.stream().map(Node::getId).toList();
        for (Map.Entry<Integer, Color> stud : students.entrySet()){
            if(! studsBeforeIDs.contains("student" + stud.getKey())){
                Pair<Integer, Integer> freeSpot = freeEntranceSpots.get(0);
                StudentView studToAdd = new StudentView(stud.getKey(), "student", stud.getValue().toString().toLowerCase(),StudentView.studentSize);
                entrance.add(studToAdd, freeSpot.getKey(), freeSpot.getValue());
                freeEntranceSpots.remove(freeSpot);
                GridPane.setHalignment(studToAdd, HPos.CENTER);
            }
        }
    }

    public void updateTable(Color tableColor, List<Integer> students){
        removeOldStudentFromTable(tableColor, students);
        addNewStudentsToTable(tableColor, students);
    }

    private  void removeOldStudentFromTable(Color tableColor, List<Integer> students){
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

    private void addNewStudentsToTable(Color tableColor, List<Integer> students){
        List<Pair<Integer,Integer>> freeTableSpots =
                new ArrayList<>(freeDRSpots.stream().filter(spot -> spot.getValue().equals(tableOrder.get(tableColor))).toList());
        List<Node> studsBefore = tables.get(tableColor).getChildren();
        List<String> studsBeforeIDs = studsBefore.stream().map(Node::getId).toList();
        for (Integer stud : students){
            if(! studsBeforeIDs.contains("student" + stud)){
                Pair<Integer, Integer> freeSpot = freeTableSpots.get(0);
                StudentView studToAdd = new StudentView(stud, "student", tableColor.toString().toLowerCase(),StudentView.studentSize);
                tables.get(tableColor).add(studToAdd, freeSpot.getKey(), 0);
                freeTableSpots.remove(freeSpot);
                freeDRSpots.remove(freeSpot);
                GridPane.setHalignment(studToAdd, HPos.CENTER);
            }
        }
    }

    public void updateProfessors(List<Color> newProfessorsOwned){ //This isn't optimized like others, but now I have no will (also it's at max 5 pngs to reload I don't : care)
        professors.getChildren().clear();
        for(Color profColor : newProfessorsOwned){
            professors.add(new PawnView(0, "professor", profColor.toString().toLowerCase(), StudentView.studentSize),
                        0, tableOrder.get(profColor));
        }
    }

    public void updateTowers(int newNumOfTowers){
        if(newNumOfTowers < numOfTowers){
            towerSpace.getChildren().clear();
            for (int i = 0; i < newNumOfTowers ; i++) {
                towerSpace.add(new PawnView(-1, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize),
                        i%2, i/2);
            }
        }
        else if(newNumOfTowers > numOfTowers){
            for (int i = numOfTowers; i < newNumOfTowers ; i++) {
                towerSpace.add(new PawnView(-1, "tower", towerColor.toString().toLowerCase(), PawnView.pawnSize),
                        i%2, i/2);
            }
        }
        this.numOfTowers = newNumOfTowers;
    }

    public void debugPawn(){
        debugStudE();
        debugStudDR();
        debugP();
        debugT();
    }

    private void debugStudE(){
        for (int i = 0; i < 9; i++) {
            StudentView studentView = new StudentView(0, "student", "green", StudentView.studentSize);
            entrance.add(studentView, i % 2, i / 2);
            GridPane.setHalignment(studentView, HPos.CENTER);
        }
    }

    private void debugStudDR(){
        for(Color color : Color.values()){
            for (int i = 0; i < 10; i++) {
                StudentView studentView = new StudentView(0, "student", color.toString().toLowerCase(), StudentView.studentSize);
                tables.get(color).add(studentView, i, 0);
                GridPane.setHalignment(studentView, HPos.CENTER);
            }
        }
    }

    private void debugP(){
        for(Color color : Color.values()){
            PawnView prof = new PawnView(0, "professor", color.toString().toLowerCase(), StudentView.studentSize);
            professors.add(prof, 0, tableOrder.get(color));
            GridPane.setHalignment(prof, HPos.CENTER);
        }
    }

    private void debugT(){
        for (int i = 0; i < 8; i++) {
            PawnView tower = new PawnView(0, "tower", "black", PawnView.pawnSize);
            towerSpace.add(tower, i % 2, i / 2);
            GridPane.setHalignment(tower, HPos.CENTER);
        }
    }
}
