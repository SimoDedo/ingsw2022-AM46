package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
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

/**
 * This class contains a character card: its image, its StudentContainerPane space, its no-entry tile space and its color
 * selection pane space.
 */
public class CharacterPane extends StackPane {

    /**
     * The height of a character card.
     */
    public static final double charHeight = 100.0;

    /**
     * The width of a character card.
     */
    public static final double charWidth = charHeight/2;

    /**
     * The StudentContainerPane on this character.
     */
    private StudentContainerPane studentPane;

    /**
     * The no-entry tile image on this character (optionally visible).
     */
    private ImageView noEntryTile;

    /**
     * The no-entry tile counter.
     */
    private Text noEntryTileText;

    /**
     * The ColorSelectionPane on this character (optionally visible).
     */
    private ColorSelectionPane colorSelectionPane;

    /**
     * The coin overcharge image.
     */
    private final ImageView coinOvercharge;

    /**
     * The number of uses left.
     */
    private final Text usesLeft;

    /**
     * A list containing the "coordinates" (column and row) of the free spots in the character's student pane.
     */
    private List<Pair<Integer, Integer>> freeStudSpots;

    /**
     * The color chosen by the user.
     */
    private Color colorChosen;

    /**
     * The current effect applied to this character image.
     */
    protected Effect currentEffect;

    /**
     * Constructor for the class. Sets it as disabled and creates the coin overcharge and the uses left, setting them to
     * invisible.
     */
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

    /**
     * Creates the character with the given ID. Creates the student pane and the color selection pane.
     * @param charID the character ID
     */
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

    /**
     * Sets the character image with the given ID.
     * @param charID the ID of the character of which to retrieve the image
     */
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

    /**
     * Updates a character with all the needed updated information.
     * @param isActive true if the character is active, false otherwise
     * @param usesLeft the number of uses left
     * @param newStuds a map containing the student IDs and their respective color
     * @param numOfNoEntryTiles the number of no-entry tiles on this character
     * @param isOvercharged true if the character is overcharged (it costs one additional coin), false otherwise
     */
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
            this.currentEffect = Effects.activatedCharacterShadow;
            charView.setEffect(currentEffect);
            if(usesLeft > 0){
                this.usesLeft.setText(Integer.toString(usesLeft));
                this.usesLeft.setVisible(true);
            }
            else
                this.usesLeft.setVisible(false);
        }
        else {
            this.currentEffect = Effects.disabledCharacterShadow;
            this.usesLeft.setVisible(false);
        }

    }

    /**
     * Removes students from the character that aren't present on the card anymore.
     * @param newStuds an updated map with the student IDs and their respective color
     */
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

    /**
     * Adds students to the character that weren't present on the card but are now.
     * @param newStuds an updated map with the student IDs and their respective color
     */
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

    /**
     * Getter for the students ImageViews on this character.
     * @return a list of student ImageViews
     */
    public List<StudentView> getStudents() {
        return studentPane.getStudents();
    }

    /**
     * Setter for the color chosen.
     * @param color the chosen color
     */
    public void setColor(Color color) {
        colorChosen = color;
    }

    /**
     * Getter for the color chosen.
     * @return the chosen color
     */
    public Color getColorChosen() {
        return colorChosen;
    }

}
