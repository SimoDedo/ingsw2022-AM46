package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharContainerPane extends HBox {

    static double charContainerHeight = 80.0, charContainerWidth = 300.0;

    private final GUIController controller;

    private int characterChosen;

    private List<Integer> charIDs;
    private int studentChosen;

    public CharContainerPane(GUIController controller) {
        super(5.0);
        this.controller = controller;
        this.setId("charContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(charContainerWidth, charContainerHeight);

    }

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

    public void updateCharacter(int ID, boolean isActive, int usesLeft,HashMap<Integer, Color> newStuds, int numOfNoEntryTiles, boolean isOvercharged){
        ((CharacterPane)this.lookup("#characterPane" + ID)).updateCharacter(isActive, usesLeft,newStuds, numOfNoEntryTiles, isOvercharged);
    }

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
                controller.notifyCharacter();
            });
        }
    }

    public void setCharacterChosen(int charIndex) {
        this.characterChosen = charIndex;
    }

    public int getCharacterChosen() {
        return characterChosen;
    }

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
                characterPane.setOnMouseClicked(event -> {
                });
            }
        }
    }

    public void enableActivateCharacter() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        ImageView charImageView = (ImageView) characterPane.lookup("#charView");

        charImageView.setEffect(Effects.activatedCharacterShadow);
        characterPane.currentEffect = Effects.activatedCharacterShadow;
        characterPane.setOnMouseEntered(e -> charImageView.setEffect(Effects.hoveringCharacterShadow));
        characterPane.setOnMouseExited(e -> charImageView.setEffect(Effects.activatedCharacterShadow));
        characterPane.setOnMouseClicked(event -> controller.notifyAbility());
    }

    public void enableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        for (StudentView student : characterPane.getStudents()) {
            student.setEnabled();
            student.setCallback(event -> {
                setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                controller.notifyStudentChar();
            });
        }
    }

    public void disableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        for (StudentView student : characterPane.getStudents()) {
            student.setDisabled();
            student.setCallback(event -> {
            });
        }
    }

    public void setStudentChosen(int studentID) {
        this.studentChosen = studentID;
    }

    public int getStudentChosen() {
        return studentChosen;
    }

    public void disableActivateCharacter() {
        disableSelectCharacter();
    }

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
                    controller.notifyColorChar();
                });
            }
        }
    }

    public void disableSelectColor() {
        if(characterChosen > 0 && characterChosen< 13){
            ColorSelectionPane colorPane = (ColorSelectionPane) this.lookup("#char" + characterChosen + "ColorPane");
            colorPane.setVisible(false);
            for (Node node : colorPane.getChildren()) {
                node.setEffect(Effects.disabledStudentShadow);
                node.setOnMouseEntered(event -> {});
                node.setOnMouseExited(event -> {});
                node.setOnMouseClicked(event -> {
                });
            }
        }
    }

    public void debugStud(){
        for (Integer charID : charIDs) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
            if (characterPane != null)
                characterPane.debugStud();
        }
    }
}
