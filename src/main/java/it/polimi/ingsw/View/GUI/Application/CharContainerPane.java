package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharContainerPane extends HBox {

    static double charContainerHeight = 80.0, charContainerWidth = 300.0;

    private final GUIController controller;

    private int characterChosen;

    private List<Integer> charIDs;


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

    public void updateCharacter(int ID, HashMap<Integer, Color> newStuds, int numOfNoEntryTiles){
        ((CharacterPane)this.lookup("#characterPane" + ID)).updateCharacter(newStuds, numOfNoEntryTiles);
    }

    public void enableSelectCharacter() {
        for (Integer charID : charIDs) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
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
        for (Integer charID : charIDs) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charID);
            characterPane.setOnMouseClicked(event -> {
                System.out.println("I'm disabled");
            });
        }
    }

    public void enableActivateCharacter() {
        CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + characterChosen);
        characterPane.setOnMouseClicked(event -> {
            System.out.println("Someone clicked on me for the second time! Ability started " + characterPane.getId());
            controller.prepareAbility();
        });
    }

    public void debugStud(){
        for (int i = 0; i < charIDs.size(); i++) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + charIDs.get(i));
            if(characterPane != null)
                characterPane.debugStud();
        }
    }
}
