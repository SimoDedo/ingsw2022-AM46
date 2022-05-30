package it.polimi.ingsw.View.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardPane extends StackPane {

    private final double studentSize = 15.0, modifierSize = 25.0; //CHECKME: uniform better with the rest
    private double sizeBoardV;
    private double sizeBoardH;
    private final double entrancePct = 500.0/3304.0; //TODO: not hardcoded but fetched form image file (at least 3304.0)
    private final double diningRoomPct = (2270.0 - 500.0) /3304.0;
    private final double professorPct = (2560.0 - 2270.0) / 3304.0;
    private final double towerPct = (3304.0 - 2560.0) / 3304.0;


    private final GridPane mainGrid;
    private final GridPane entrance;
    private final GridPane diningRoom;
    private final GridPane professors;
    private final GridPane towerSpace;


    public BoardPane(int position, double sizeBoardV) {
        this.sizeBoardV = sizeBoardV;
        this.sizeBoardH = sizeBoardV * (3304.0 / 1413.0);

        this.setId("boardPane" + position);
        Image playerBoard = new Image("/world/board_roundedcorners.png", 600, 600,true, false);
        ImageView imageViewPB = new ImageView(playerBoard);
        imageViewPB.setPreserveRatio(true);
        imageViewPB.setFitHeight(sizeBoardV);
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

        entrance = new GridPane();
        entrance.setId("entrancePane" + position);
        createGrid(entrance, entrancePct, 5, 2, 10, 5, 15);

        diningRoom = new GridPane();
        diningRoom.setId("diningRoomPane" + position);
        createGrid(diningRoom, diningRoomPct, 5, 10, 5, 7.5, 0);

        professors = new GridPane();
        professors.setId("professorsPane" + position);
        createGrid(professors, professorPct, 5, 1, 6, 7.5, 0);

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
        toCreate.setPadding(new Insets(sizeBoardV * (paddingVPct/100), sizeBoardH * (paddingHPct/100 + paddingExtraRightPct/100) * widthPct,
                sizeBoardV * (paddingVPct/100), sizeBoardH * (paddingHPct/100) * widthPct));

        //toCreate.setGridLinesVisible(true);
    }


    public void debugStudE(){
        Image red1 = new Image("/pawns/student_red.png", 50, 50, true, true);
        ImageView redView1 = new ImageView(red1);
        redView1.setEffect(new DropShadow(10.0, Color.BLACK));
        redView1.setPreserveRatio(true);
        redView1.setFitHeight(studentSize);
        redView1.setSmooth(true);
        redView1.setCache(true);

        Image red2 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView2 = new ImageView(red2);
        redView2.setEffect(new DropShadow(10.0, Color.BLACK));
        redView2.setPreserveRatio(true);
        redView2.setFitHeight(studentSize);
        redView2.setSmooth(true);
        redView2.setCache(true);

        Image red3 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView3 = new ImageView(red3);
        redView3.setEffect(new DropShadow(10.0, Color.BLACK));
        redView3.setPreserveRatio(true);
        redView3.setFitHeight(studentSize);
        redView3.setSmooth(true);
        redView3.setCache(true);

        Image red4 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView4 = new ImageView(red4);
        redView4.setEffect(new DropShadow(10.0, Color.BLACK));
        redView4.setPreserveRatio(true);
        redView4.setFitHeight(studentSize);
        redView4.setSmooth(true);
        redView4.setCache(true);

        Image red5 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView5 = new ImageView(red5);
        redView5.setEffect(new DropShadow(10.0, Color.BLACK));
        redView5.setPreserveRatio(true);
        redView5.setFitHeight(studentSize);
        redView5.setSmooth(true);
        redView5.setCache(true);

        Image red6 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView6 = new ImageView(red6);
        redView6.setEffect(new DropShadow(10.0, Color.BLACK));
        redView6.setPreserveRatio(true);
        redView6.setFitHeight(studentSize);
        redView6.setSmooth(true);
        redView6.setCache(true);

        Image red7 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView7 = new ImageView(red7);
        redView7.setEffect(new DropShadow(10.0, Color.BLACK));
        redView7.setPreserveRatio(true);
        redView7.setFitHeight(studentSize);
        redView7.setSmooth(true);
        redView7.setCache(true);

        Image red8 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView8 = new ImageView(red8);
        redView8.setEffect(new DropShadow(10.0, Color.BLACK));
        redView8.setPreserveRatio(true);
        redView8.setFitHeight(studentSize);
        redView8.setSmooth(true);
        redView8.setCache(true);

        Image red9 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView9 = new ImageView(red9);
        redView9.setEffect(new DropShadow(10.0, Color.BLACK));
        redView9.setPreserveRatio(true);
        redView9.setFitHeight(studentSize);
        redView9.setSmooth(true);
        redView9.setCache(true);

        entrance.add(redView1, 0,0);
        entrance.add(redView2, 1,0);
        entrance.add(redView3, 0,1);
        entrance.add(redView4, 1,1);
        entrance.add(redView5, 0,2);
        entrance.add(redView6, 1,2);
        entrance.add(redView7, 0,3);
        entrance.add(redView8, 1,3);
        entrance.add(redView9, 0,4);

        GridPane.setHalignment(redView1, HPos.CENTER);
        GridPane.setHalignment(redView2, HPos.CENTER);
        GridPane.setHalignment(redView3, HPos.CENTER);
        GridPane.setHalignment(redView4, HPos.CENTER);
        GridPane.setHalignment(redView5, HPos.CENTER);
        GridPane.setHalignment(redView6, HPos.CENTER);
        GridPane.setHalignment(redView7, HPos.CENTER);
        GridPane.setHalignment(redView8, HPos.CENTER);
        GridPane.setHalignment(redView9, HPos.CENTER);
    }

    public void debugStudDN(){
        Image red1 = new Image("/pawns/student_red.png", 50, 50, true, true);
        ImageView redView1 = new ImageView(red1);
        redView1.setEffect(new DropShadow(10.0, Color.BLACK));
        redView1.setPreserveRatio(true);
        redView1.setFitHeight(studentSize);
        redView1.setSmooth(true);
        redView1.setCache(true);
        diningRoom.add(redView1, 0,0);

        Image red2 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView2 = new ImageView(red2);
        redView2.setEffect(new DropShadow(10.0, Color.BLACK));
        redView2.setPreserveRatio(true);
        redView2.setFitHeight(studentSize);
        redView2.setSmooth(true);
        redView2.setCache(true);
        diningRoom.add(redView2, 1,0);

        Image red3 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView3 = new ImageView(red3);
        redView3.setEffect(new DropShadow(10.0, Color.BLACK));
        redView3.setPreserveRatio(true);
        redView3.setFitHeight(studentSize);
        redView3.setSmooth(true);
        redView3.setCache(true);
        diningRoom.add(redView3, 2,0);

        Image red4 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView4 = new ImageView(red4);
        redView4.setEffect(new DropShadow(10.0, Color.BLACK));
        redView4.setPreserveRatio(true);
        redView4.setFitHeight(studentSize);
        redView4.setSmooth(true);
        redView4.setCache(true);
        diningRoom.add(redView4, 3, 0);

        Image red5 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView5 = new ImageView(red5);
        redView5.setEffect(new DropShadow(10.0, Color.BLACK));
        redView5.setPreserveRatio(true);
        redView5.setFitHeight(studentSize);
        redView5.setSmooth(true);
        redView5.setCache(true);
        diningRoom.add(redView5, 6,4);

        Image red6 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView6 = new ImageView(red6);
        redView6.setEffect(new DropShadow(10.0, Color.BLACK));
        redView6.setPreserveRatio(true);
        redView6.setFitHeight(studentSize);
        redView6.setSmooth(true);
        redView6.setCache(true);
        diningRoom.add(redView6, 4,0);

        Image red7 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView7 = new ImageView(red7);
        redView7.setEffect(new DropShadow(10.0, Color.BLACK));
        redView7.setPreserveRatio(true);
        redView7.setFitHeight(studentSize);
        redView7.setSmooth(true);
        redView7.setCache(true);
        diningRoom.add(redView7, 5,0);

        Image red8 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView8 = new ImageView(red8);
        redView8.setEffect(new DropShadow(10.0, Color.BLACK));
        redView8.setPreserveRatio(true);
        redView8.setFitHeight(studentSize);
        redView8.setSmooth(true);
        redView8.setCache(true);
        diningRoom.add(redView8, 6,0);

        Image red9 = new Image("/pawns/student_yellow.png", 50, 50, true, true);
        ImageView redView9 = new ImageView(red9);
        redView9.setEffect(new DropShadow(10.0, Color.BLACK));
        redView9.setPreserveRatio(true);
        redView9.setFitHeight(studentSize);
        redView9.setSmooth(true);
        redView9.setCache(true);
        diningRoom.add(redView9, 7,0);

        GridPane.setHalignment(redView1, HPos.CENTER);
        GridPane.setHalignment(redView2, HPos.CENTER);
        GridPane.setHalignment(redView3, HPos.CENTER);
        GridPane.setHalignment(redView4, HPos.CENTER);
        GridPane.setHalignment(redView5, HPos.CENTER);
        GridPane.setHalignment(redView6, HPos.CENTER);
        GridPane.setHalignment(redView7, HPos.CENTER);
        GridPane.setHalignment(redView8, HPos.CENTER);
        GridPane.setHalignment(redView9, HPos.CENTER);

    }

    public void debugP(){
        Image red1 = new Image("/pawns/teacher_green.png", 50, 50, true, true);
        ImageView redView1 = new ImageView(red1);
        redView1.setEffect(new DropShadow(10.0, Color.BLACK));
        redView1.setPreserveRatio(true);
        redView1.setFitHeight(studentSize);
        redView1.setSmooth(true);
        redView1.setCache(true);

        Image red2 = new Image("/pawns/teacher_red.png", 50, 50, true, true);
        ImageView redView2 = new ImageView(red2);
        redView2.setEffect(new DropShadow(10.0, Color.BLACK));
        redView2.setPreserveRatio(true);
        redView2.setFitHeight(studentSize);
        redView2.setSmooth(true);
        redView2.setCache(true);

        Image red3 = new Image("/pawns/teacher_yellow.png", 50, 50, true, true);
        ImageView redView3 = new ImageView(red3);
        redView3.setEffect(new DropShadow(10.0, Color.BLACK));
        redView3.setPreserveRatio(true);
        redView3.setFitHeight(studentSize);
        redView3.setSmooth(true);
        redView3.setCache(true);

        Image red4 = new Image("/pawns/teacher_pink.png", 50, 50, true, true);
        ImageView redView4 = new ImageView(red4);
        redView4.setEffect(new DropShadow(10.0, Color.BLACK));
        redView4.setPreserveRatio(true);
        redView4.setFitHeight(studentSize);
        redView4.setSmooth(true);
        redView4.setCache(true);

        Image red5 = new Image("/pawns/teacher_blue.png", 50, 50, true, true);
        ImageView redView5 = new ImageView(red5);
        redView5.setEffect(new DropShadow(10.0, Color.BLACK));
        redView5.setPreserveRatio(true);
        redView5.setFitHeight(studentSize);
        redView5.setSmooth(true);
        redView5.setCache(true);

        professors.add(redView1, 0, 0);
        professors.add(redView2, 0, 1);
        professors.add(redView3, 0, 2);
        professors.add(redView4, 0, 3);
        professors.add(redView5, 0, 4);

        GridPane.setHalignment(redView1, HPos.CENTER);
        GridPane.setHalignment(redView2, HPos.CENTER);
        GridPane.setHalignment(redView3, HPos.CENTER);
        GridPane.setHalignment(redView4, HPos.CENTER);
        GridPane.setHalignment(redView5, HPos.CENTER);
    }

    public void debugT(){
        for (int i = 0; i < 8; i++) {
            Image tower1 = new Image("/pawns/tower_white1.png", 50, 50, true, true);
            ImageView towerView1 = new ImageView(tower1);
            towerView1.setEffect(new DropShadow(10.0, Color.GREY));
            towerView1.setPreserveRatio(true);
            towerView1.setFitHeight(modifierSize);
            towerView1.setSmooth(true);
            towerView1.setCache(true);

            int col = i % 2;
            int row = i / 2;
            towerSpace.add(towerView1, col, row);
            GridPane.setHalignment(towerView1, HPos.CENTER);
        }
    }
}
