package it.polimi.ingsw.View.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.HashMap;
import java.util.Map;

public class BoardPane extends StackPane {

    private double boardHeight;
    private double boardWidth;
    private final double entrancePct = (500.0/3304.0) * 100; //TODO: not hardcoded but fetched form image file (at least 3304.0)
    private final double diningRoomPct = ((2270.0 - 500.0) /3304.0) * 100;
    private final double professorPct = ((2560.0 - 2270.0) / 3304.0) * 100;
    private final double towerPct = ((3304.0 - 2560.0) / 3304.0) * 100;

    private final HashMap<Color, Integer> tableOrder;

    private final GridPane mainGrid;
    private final StudentContainerPane entrance;
    private final GridPane diningRoom;
    private final HashMap<Color, StudentContainerPane> tables;
    private final StudentContainerPane professors;
    private final GridPane towerSpace;


    public BoardPane(int position, double boardHeight) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardHeight * (3304.0 / 1413.0);

        this.setId("boardPane" + position);
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

        entrance = new StudentContainerPane(boardWidth, boardHeight, entrancePct, 5, 2, 10.0, 5.0, 15.0);
        entrance.setId("entrancePane" + position);

        diningRoom = new GridPane();
        createGrid(diningRoom, diningRoomPct, 5, 1, 5.0, 7.5, 0.0);
        diningRoom.setId("diningRoomPane" + position);

        tableOrder = new HashMap<>(Map.of(Color.GREEN, 0, Color.RED, 1, Color.YELLOW, 2, Color.PINK, 3, Color.BLUE, 4));
        tables = new HashMap<>();

        for (Color color : Color.values()){
            StudentContainerPane table = new StudentContainerPane(boardWidth, boardHeight, diningRoomPct, 1, 10, 0, 0, 0);
            table.setId("table"+color.toString().toLowerCase()+position);
            tables.put(color, table);
            diningRoom.add(table, 0, tableOrder.get(color));
        }


        professors = new StudentContainerPane(boardWidth, boardHeight, professorPct, 5, 1, 6.0, 7.5, 0.0);
        professors.setId("professorsPane" + position);

        towerSpace = new GridPane();
        towerSpace.setId("towerSpacePane" + position);
        createGrid(towerSpace, towerPct, 4, 2, 12, 10, 0);


        debugStudE();
        debugStudDN();
        debugP();
        debugT();


        mainGrid.add(entrance, 0 , 0);
        mainGrid.add(diningRoom, 1, 0);
        mainGrid.add(professors,2,0);
        mainGrid.add(towerSpace, 3, 0);
        //mainGrid.setGridLinesVisible(true);
        this.getChildren().add(mainGrid);
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

        //toCreate.setGridLinesVisible(true);
    }


    public void debugStudE(){
        for (int i = 0; i < 9; i++) {
            StudentView studentView = new StudentView(0, "student", "green", StudentView.studentSize);
            entrance.add(studentView, i % 2, i / 2);
            GridPane.setHalignment(studentView, HPos.CENTER);
        }
    }

    public void debugStudDN(){
        for(Color color : Color.values()){
            for (int i = 0; i < 10; i++) {
                StudentView studentView = new StudentView(0, "student", color.toString().toLowerCase(), StudentView.studentSize);
                tables.get(color).add(studentView, i, 0);
                GridPane.setHalignment(studentView, HPos.CENTER);
            }
        }
    }

    public void debugP(){
        for(Color color : Color.values()){
            PawnView prof = new PawnView(0, "professor", color.toString().toLowerCase(), StudentView.studentSize);
            professors.add(prof, 0, tableOrder.get(color));
            GridPane.setHalignment(prof, HPos.CENTER);
        }

    }

    public void debugT(){
        for (int i = 0; i < 8; i++) {
            PawnView tower = new PawnView(0, "tower", "white", PawnView.pawnSize);
            towerSpace.add(tower, i % 2, i / 2);
            GridPane.setHalignment(tower, HPos.CENTER);
        }
    }
}
