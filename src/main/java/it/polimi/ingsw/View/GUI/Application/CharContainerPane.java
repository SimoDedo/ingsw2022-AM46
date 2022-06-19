package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.ObservableGUI;
import it.polimi.ingsw.View.GUI.ObserverGUI;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the character cards (with their respective information).
 */
public class CharContainerPane extends HBox implements ObservableGUI {

    /**
     * The height of this pane.
     */
    static final double charContainerHeight = 80.0;

    /**
     * The width of this pane.
     */
    static final double charContainerWidth = 300.0;

    private ObserverGUI observer;

    /**
     * The ID of the chosen character.
     */
    private int characterChosen;

    /**
     * A list containing the ID of the characters available on the board.
     */
    private List<Integer> charIDs;

    /**
     * The ID of the chosen student.
     */
    private int studentChosen;

    /**
     * Constructor for the class. Sets its ID, size and alignment.
     */
    public CharContainerPane() {
        super(5.0);
        this.setId("charContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(charContainerWidth, charContainerHeight);

    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
    }

    /**
     * Creates the given number of characters with their respective ID.
     * @param characters a list containing the ID of the characters to add to this pane
     */
    public void createCharacters(List<Integer> characters) {
        this.charIDs = new ArrayList<>();
        for (int i = 0; i < characters.size(); i++) {
            CharacterPane characterPane = new CharacterPane();
            characterPane.setId("characterPane" + characters.get(i));
            this.charIDs.add(characters.get(i));
            this.getChildren().add(characterPane);
            characterPane.createCharacter(charIDs.get(i));
        }
    }

    /**
     * Updates the character with the given ID with all the needed updated information.
     * @param ID the ID of the character to update
     * @param isActive true if the character is active, false otherwise
     * @param usesLeft the number of uses left
     * @param newStuds a map containing the student IDs and their respective color
     * @param numOfNoEntryTiles the number of no-entry tiles on this character
     * @param isOvercharged true if the character is overcharged (it costs one additional coin), false otherwise
     */
    public void updateCharacter(int ID, boolean isActive, int usesLeft,HashMap<Integer, Color> newStuds, int numOfNoEntryTiles, boolean isOvercharged){
        ((CharacterPane)this.lookup("#characterPane" + ID)).updateCharacter(isActive, usesLeft,newStuds, numOfNoEntryTiles, isOvercharged);
    }

    /**
     * Enables the selection of any character in this pane.
     */
    public void enableSelectCharacter() {
        for (Integer charID : charIDs) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
            ImageView charImageView = (ImageView) characterPane.lookup("#charView");
            charImageView.setEffect(Effects.enabledCharacterShadow);
            characterPane.currentEffect = Effects.enabledCharacterShadow;
            characterPane.setOnMouseEntered(e -> charImageView.setEffect(Effects.hoveringCharacterShadow));
            characterPane.setOnMouseExited(e -> charImageView.setEffect(Effects.enabledCharacterShadow));
            int charIndex = charID;
            characterPane.setOnMouseClicked(event -> {
                setCharacterChosen(charIndex);
                observer.notifyCharacter();
            });
        }
    }

    /**
     * Setter for the character chosen.
     * @param charIndex the ID of the chosen character
     */
    public void setCharacterChosen(int charIndex) {
        this.characterChosen = charIndex;
    }

    /**
     * Getter for the character chosen.
     * @return the ID of the chosen character
     */
    public int getCharacterChosen() {
        return characterChosen;
    }

    /**
     * Disables the selection of any character.
     */
    public void disableSelectCharacter() {
        if(charIDs != null){
            for (Integer charID : charIDs) {
                CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
                ImageView charImageView = (ImageView) characterPane.lookup("#charView");
                if(characterPane.currentEffect != Effects.activatedCharacterShadow){
                    characterPane.currentEffect = Effects.disabledCharacterShadow;
                    charImageView.setEffect(Effects.disabledCharacterShadow);
                }
                else
                    charImageView.setEffect(characterPane.currentEffect);
                characterPane.setOnMouseEntered(e -> {});
                characterPane.setOnMouseExited(e -> {});
                characterPane.setOnMouseClicked(event -> {});
            }
        }
    }

    /**
     * Enables the activation of a character (starting its ability).
     */
    public void enableActivateCharacter() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        ImageView charImageView = (ImageView) characterPane.lookup("#charView");

        charImageView.setEffect(Effects.activatedCharacterShadow);
        characterPane.currentEffect = Effects.activatedCharacterShadow;
        characterPane.setOnMouseEntered(e -> charImageView.setEffect(Effects.hoveringCharacterShadow));
        characterPane.setOnMouseExited(e -> charImageView.setEffect(Effects.activatedCharacterShadow));
        characterPane.setOnMouseClicked(event -> observer.notifyAbility());
    }

    /**
     * Enables the selection of students on any character.
     */
    public void enableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        for (StudentView student : characterPane.getStudents()) {
            student.setEnabled();
            student.setCallback(event -> {
                setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                observer.notifyStudentChar();
            });
        }
    }

    /**
     * Disables the selection of students on any character.
     */
    public void disableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        for (StudentView student : characterPane.getStudents()) {
            student.setDisabled();
            student.setCallback(event -> {});
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
     * Disables the activation of any character.
     */
    public void disableActivateCharacter() {
        disableSelectCharacter();
    }

    /**
     * Enables the selection of a color from the active character's color selection pane.
     */
    public void enableSelectColor() {
        if(characterChosen > 0 && characterChosen< 13) {
            ColorSelectionPane colorPane = (ColorSelectionPane) this.lookup("#char" + characterChosen + "ColorPane");
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
            colorPane.setVisible(true);
            for (Map.Entry<Color, Integer> entry : ColorSelectionPane.colorOrder.entrySet()) {
                colorPane.getChildren().get(entry.getValue()).setEffect(Effects.enabledStudentShadow);
                colorPane.getChildren().get(entry.getValue()).setOnMouseEntered(event -> {
                    colorPane.getChildren().get(entry.getValue()).setEffect(Effects.hoveringStudentShadow);
                });
                colorPane.getChildren().get(entry.getValue()).setOnMouseExited(event -> {
                    colorPane.getChildren().get(entry.getValue()).setEffect(Effects.enabledStudentShadow);
                });
                colorPane.getChildren().get(entry.getValue()).setOnMouseClicked(event -> {
                    characterPane.setColor(entry.getKey());
                    observer.notifyColorChar();
                });
            }
        }
    }

    /**
     * Disables the selection of a color from the active character's color selection pane.
     */
    public void disableSelectColor() {
        if(characterChosen > 0 && characterChosen< 13){
            ColorSelectionPane colorPane = (ColorSelectionPane) this.lookup("#char" + characterChosen + "ColorPane");
            colorPane.setVisible(false);
            for (Node node : colorPane.getChildren()) {
                node.setEffect(Effects.disabledStudentShadow);
                node.setOnMouseEntered(event -> {});
                node.setOnMouseExited(event -> {});
                node.setOnMouseClicked(event -> {});
            }
        }
    }

}
