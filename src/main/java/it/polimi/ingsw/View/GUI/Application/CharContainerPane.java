package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
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

    public void updateCharacter(int ID, HashMap<Integer, Color> newStuds, int numOfNoEntryTiles, boolean isOvercharged){
        ((CharacterPane)this.lookup("#characterPane" + ID)).updateCharacter(newStuds, numOfNoEntryTiles, isOvercharged);
    }

    public void enableSelectCharacter() {
        for (Integer charID : charIDs) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
            ImageView charImageView = (ImageView) characterPane.lookup("#charView");
            charImageView.setEffect(new DropShadow(50.0, javafx.scene.paint.Color.WHITE));
            int charIndex = charID;
            characterPane.setOnMouseClicked(event -> {
                System.out.println("Someone clicked on me!" + characterPane.getId());
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
                charImageView.setEffect(new DropShadow());
                characterPane.setOnMouseClicked(event -> {
                    System.out.println("I'm a disabled character");
                });
            }
        }
    }

    public void enableActivateCharacter() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        characterPane.setEffect(new DropShadow(50.0, javafx.scene.paint.Color.WHITE));
        characterPane.setOnMouseClicked(event -> {
            System.out.println("Someone clicked on me for the second time! Ability started " + characterPane.getId());
            controller.notifyAbility();
        });
    }

    public void enableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        characterPane.setEffect(new DropShadow());
        for (StudentView student : characterPane.getStudents()) {
            student.setEnabled();
            student.setCallback(event -> {
                System.out.println("Someone clicked on a student in an activated character! " + student.getId());
                setStudentChosen(Integer.parseInt(student.getId().substring("student".length())));
                controller.notifyStudentChar();
            });
        }
    }

    public void disableSelectStudents() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        characterPane.setEffect(new DropShadow());
        for (StudentView student : characterPane.getStudents()) {
            student.setDisabled();
            student.setCallback(event -> {
                System.out.println("I'm a disable student character! " + student.getId());
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
                colorPane.getChildren().get(entry.getValue()).setOnMouseClicked(event -> {
                    characterPane.setColor(entry.getKey());
                    System.out.println("no vabbè hai premuto un colore, assurdo");
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
                node.setOnMouseClicked(event -> {
                    System.out.println("I'm a disabled color");
                });
            }
        }
    }

    public void debugStud(){
        for (int i = 0; i < charIDs.size(); i++) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charIDs.get(i));
            if(characterPane != null)
                characterPane.debugStud();
        }
    }
}
