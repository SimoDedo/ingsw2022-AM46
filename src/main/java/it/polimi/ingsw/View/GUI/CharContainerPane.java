package it.polimi.ingsw.View.GUI;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.List;

public class CharContainerPane extends HBox {

    static double charContainerHeight = 80.0, charContainerWidth = 300.0;

    private final GUIController controller;

    private int characterChosen;

    public CharContainerPane(GUIController controller) {
        super(5.0);
        this.controller = controller;
        this.setId("charContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(charContainerWidth, charContainerHeight);
    }

    public void createCharacters(List<Integer> charIDs) {
        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = new CharacterPane();
            characterPane.setId("characterPane" + i);
            this.getChildren().add(characterPane);
            characterPane.createCharacter(charIDs.get(i));
        }
    }

    public void enableSelectCharacter() {
        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + i);
            int charIndex = i;
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
        for (int i = 0; i < 3; i++) {
            CharacterPane characterPane = (CharacterPane) this.lookup("#characterPane" + i);
            characterPane.setOnMouseClicked(event -> {
                System.out.println("I'm disabled");
            });
        }
    }
}
