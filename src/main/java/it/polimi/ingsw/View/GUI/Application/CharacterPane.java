package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CharacterPane extends StackPane {

    public static double charHeight = 100.0, charWidth = charHeight/2;

    private StudentContainerPane studentPane;

    private ImageView noEntryTile;

    private Text noEntryTileText;

    private ColorSelectionPane colorSelectionPane;

    private final ImageView coinOvercharge;

    private final Text usesLeft;

    private List<Pair<Integer, Integer>> freeStudSpots;

    private Color colorChosen;

    protected Effect currentEffect;

    public CharacterPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(charWidth, charHeight);
        currentEffect = Effects.disabledCharacterShadow;

        Image character = new Image("/chars/char_back.png");
        ImageView charView = new ImageView(character);
        charView.setId("charView");
        charView.setEffect(new DropShadow());
        charView.setPreserveRatio(true);
        charView.setFitHeight(charHeight);
        charView.setSmooth(true);
        charView.setCache(true);
        this.getChildren().add(charView);

        Image coin = new Image("/world/coin.png", 50, 50, true, true);
        coinOvercharge = new ImageView(coin);
        coinOvercharge.setPreserveRatio(true);
        coinOvercharge.setFitHeight(PawnView.pawnSize);
        coinOvercharge.setVisible(false);
        coinOvercharge.setEffect(new DropShadow());
        coinOvercharge.setMouseTransparent(true);
        this.getChildren().add(coinOvercharge);
        StackPane.setAlignment(coinOvercharge, Pos.TOP_RIGHT);

        usesLeft = new Text("0");
        usesLeft.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 20));
        usesLeft.setFill(javafx.scene.paint.Color.WHITE);
        usesLeft.setEffect(new DropShadow());
        usesLeft.setStyle("-fx-stroke: black;");
        usesLeft.setStyle("-fx-stroke-width: 3;");
        usesLeft.setMouseTransparent(true);
        usesLeft.setVisible(false);
        this.getChildren().add(usesLeft);
        StackPane.setAlignment(usesLeft, Pos.BOTTOM_LEFT);

        this.setPickOnBounds(false);
    }

    public void createCharacter(int charID) {
        studentPane = new StudentContainerPane("characterStudentPane", charID,
                charWidth, charHeight, 100, 3, 3, 10.0, 25.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        this.getChildren().add(studentPane);
        setCharacterImage(charID);

        freeStudSpots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                freeStudSpots.add(new Pair<>(i,j));
            }
        }

        noEntryTileText = new Text();
        Image noEntry = new Image("/pawns/noentrytile.png", 50, 50, true, true);
        noEntryTile = new ImageView(noEntry);
        noEntryTile.setEffect(new DropShadow());
        noEntryTile.setPreserveRatio(true);
        noEntryTile.setFitHeight(PawnView.pawnSize);
        noEntryTile.setVisible(false);
        noEntryTile.setMouseTransparent(true);
        this.getChildren().add(noEntryTile);

        noEntryTileText.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 20));
        noEntryTileText.setFill(javafx.scene.paint.Color.WHITE);
        noEntryTileText.setEffect(new DropShadow());
        noEntryTileText.setStyle("-fx-stroke: black;");
        noEntryTileText.setStyle("-fx-stroke-width: 3;");
        noEntryTileText.setVisible(false);
        noEntryTileText.setMouseTransparent(true);
        this.getChildren().add(noEntryTileText);

        colorSelectionPane = new ColorSelectionPane(charID);
        this.getChildren().add(colorSelectionPane);
        StackPane.setAlignment(colorSelectionPane, Pos.CENTER);
    }

    public void setCharacterImage(int charID) {
        ImageView charView = (ImageView) this.lookup("#charView");
        Image newChar = new Image("/chars/char" + charID + ".png", 200, 200, true, true);
        charView.setImage(newChar);
        charView.setEffect(new DropShadow());
        charView.setPreserveRatio(true);
        charView.setFitHeight(charHeight);
        charView.setSmooth(true);
        charView.setCache(true);
    }

    public void updateCharacter(boolean isActive, int usesLeft,HashMap<Integer, Color> newStuds, int numOfNoEntryTiles, boolean isOvercharged){
        //Update students
        removeOldStuds(newStuds);
        addNewStuds(newStuds);
        //Update no entry
        if(numOfNoEntryTiles > 0){
            noEntryTile.setVisible(true);
            noEntryTileText.setText(String.valueOf(numOfNoEntryTiles));
            noEntryTileText.setVisible(true);
        }
        else {
            noEntryTile.setVisible(false);
            noEntryTileText.setVisible(false);
        }
        //Set overcharge
        coinOvercharge.setVisible(isOvercharged);
        ImageView charView = ((ImageView) this.lookup(("#charView")));
        if(isActive){
            charView.setEffect(Effects.activatedCharacterShadow);
            if(usesLeft > 0){
                this.usesLeft.setText(Integer.toString(usesLeft));
                this.usesLeft.setVisible(true);
            }
            else
                this.usesLeft.setVisible(false);
        }
        else {
            charView.setEffect(Effects.disabledCharacterShadow);
            this.usesLeft.setVisible(false);

        }

    }

    private void removeOldStuds(HashMap<Integer, Color> newStuds){
        List<Node> oldStuds = new ArrayList<>(studentPane.getChildren());
        List<String> studsNow = newStuds.keySet().stream().map(id -> "student" + id).toList();
        for(Node oldStud : oldStuds){
            if(oldStud instanceof StudentView) {
                if (!studsNow.contains(oldStud.getId())) {
                    Pair<Integer, Integer> spotToFree = new Pair<>(GridPane.getColumnIndex(oldStud), GridPane.getRowIndex(oldStud));
                    studentPane.getChildren().remove(oldStud);
                    freeStudSpots.add(spotToFree);
                }
            }
        }
    }

    private void addNewStuds(HashMap<Integer, Color> newStuds){
        List<Node> oldStuds = studentPane.getChildren();
        List<String> studsBeforeIDs = oldStuds.stream().map(Node::getId).toList();
        for (Map.Entry<Integer, Color> stud : newStuds.entrySet()){
            if(! studsBeforeIDs.contains("student" + stud.getKey())){
                int rand = ThreadLocalRandom.current().nextInt(freeStudSpots.size());
                Pair<Integer, Integer> freeSpot = freeStudSpots.get(rand);
                studentPane.add(new StudentView(stud.getKey(), "student", stud.getValue().toString().toLowerCase(),StudentView.studentSize),
                        freeSpot.getKey(), freeSpot.getValue());
                freeStudSpots.remove(rand);
            }
        }
    }

    public List<StudentView> getStudents() {
        return studentPane.getStudents();
    }

    public void setColor(Color color) {
        colorChosen = color;
    }

    public Color getColorChosen() {
        return colorChosen;
    }

    public void debugStud(){
        for (int i = 0; i < 3; i++) {
            StudentView studentView = new StudentView(i, "student", "pink", StudentView.studentSize);
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