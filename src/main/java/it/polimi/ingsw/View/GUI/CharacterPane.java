package it.polimi.ingsw.View.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CharacterPane extends StackPane {

    double charHeight = 100.0, charWidth = charHeight/2;

    private StudentContainerPane studentPane;

    public CharacterPane() {
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(charWidth, charHeight);
        Image character = new Image("/chars/char_back.png");
        ImageView charView = new ImageView(character);
        charView.setId("charView");
        charView.setEffect(new DropShadow());
        charView.setPreserveRatio(true);
        charView.setFitHeight(charHeight);
        charView.setSmooth(true);
        charView.setCache(true);
        this.getChildren().add(charView);

    }

    public void createCharacter(int ID){
        studentPane = new StudentContainerPane("characterStudentPane", ID,
                charWidth, charHeight, 100, 3, 3, 10.0, 25.0, 0.0);
        studentPane.setAlignment(Pos.CENTER);
        studentPane.setVgap(2.0);
        studentPane.setHgap(2.0);
        this.getChildren().add(studentPane);
        setCharacterImage(ID);
    }

    public void setCharacterImage(int charID) {
        ImageView charView = (ImageView) this.lookup("#charView");
        Image newChar = new Image("/chars/char" + charID + ".png", 250, 250, true, true);
        charView.setImage(newChar);
        charView.setEffect(new DropShadow(50.0, Color.WHITE));
        charView.setPreserveRatio(true);
        charView.setFitHeight(charHeight);
        charView.setSmooth(true);
        charView.setCache(true);
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
