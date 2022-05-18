package it.polimi.ingsw.View.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class for controlling the GUIController based on method calls from the client.
 */
public class GUIController {

    GUI gui;
    private GUIApplication guiApplication;

    public GUIController(GUI gui) {
        this.gui = gui;
        guiApplication = GUIApplication.getInstance();
        guiApplication.setController(this);
    }

    public void displayError(String errorDescription) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        Stage stage = (Stage) errorDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/icon.png"));
        errorDialog.setTitle("Error");
        errorDialog.setHeaderText("Wrong action!");
        errorDialog.setContentText(errorDescription + ". Please choose another move or select Help > Game Rules to get further info!");
        errorDialog.showAndWait();
    }

    public void connectToIP() {
        String IP = ( (TextField) guiApplication.lookup("ipField")).getText();
        String port = ( (TextField) guiApplication.lookup("portField")).getText();
        System.out.println("Connecting to " + IP + ":" + port); //da cambiare
        connectToIPSuccessful(); // da cambiare
    }

    public void connectToIPSuccessful() {
        guiApplication.lookup("ipPane").setDisable(true);
        guiApplication.lookup("nickPane").setDisable(false);
        ( (Button) guiApplication.lookup("connectButton")).setText("Connected successfully!");
    }

    public void connectWithNickname() {
        String nickname = ( (TextField) guiApplication.lookup("nickField")).getText();
        System.out.println("Connecting with nickname " + nickname); // da cambiare
        connectWithNicknameSuccessful(); // da cambiare
    }

    public void connectWithNicknameSuccessful() {
        guiApplication.switchToGameSetup();
    }

    public void enableGameSettings() {
        guiApplication.lookup("gameSettingsPane").setDisable(false);
        guiApplication.lookup("towerWizardPane").setDisable(true);
    }

    public void sendGameSettings() {
        // if something's missing: do nothing
        // sending stuff to client; if successful
        guiApplication.lookup("gameSettingsPane").setDisable(true);
        guiApplication.lookup("towerWizardPane").setDisable(false);
    }

    public void updateTowerWizard() {
        // called when there's an update in the game and I'm still waiting for it to start
        // ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(updatedGame.getTowerColors());
        // or something like that
    }

    public void sendTowerColor() {
        // TowerColor lookup.gettext e switch case per trasformare in enum
        // fallisce se il campo è vuoto
        //towercolor disable
        System.out.println("Tower color chosen");
    }

    public void sendWizardType() {
        // if tower color is null, don't do anything!!!!!!!!!
        System.out.println("Wizard type chosen");
        // wizard lookup.gettext e switch case per trasformare in enum
        // fallisce se il campo è vuoto
        Label label = ( (Label) guiApplication.lookup("connectionMessage"));
        label.setText("Waiting for a game to start.");
        StringProperty stringProperty = label.textProperty();
        DoubleProperty opacity = label.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start..")),
                new KeyFrame(new Duration(2000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start...")),
                new KeyFrame(new Duration(3000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start....")),
                new KeyFrame(new Duration(4000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start.....")),
                new KeyFrame(new Duration(5000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start......")),
                new KeyFrame(new Duration(6000), new KeyValue(opacity, 1.0), new KeyValue(stringProperty, "Waiting for a game to start......."))
        );
        fadeIn.play();
        startGame();
    }

    public void startGame() {
        guiApplication.switchToMain();
    }

}
