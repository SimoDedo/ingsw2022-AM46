package it.polimi.ingsw.View.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for controlling the GUIController based on method calls from the client.
 */
public class GUIController {

    GUI gui;
    private GUIApplication guiApplication;

    private List<List<Integer>> groupList = new ArrayList<>();

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
        System.out.println("Connecting to " + IP + ":" + port); // DELETEME debug
        connectToIPSuccessful(); // DELETEME debug
    }

    public void connectToIPSuccessful() {
        guiApplication.lookup("ipPane").setDisable(true);
        guiApplication.lookup("nickPane").setDisable(false);
        ( (Button) guiApplication.lookup("connectButton")).setText("Connected successfully!");
    }

    public void connectWithNickname() {
        String nickname = ( (TextField) guiApplication.lookup("nickField")).getText();
        System.out.println("Connecting with nickname " + nickname); // DELETEME debug
        connectWithNicknameSuccessful(); // DELETEME debug
    }

    public void connectWithNicknameSuccessful() {
        guiApplication.switchToGameSetup();
        enableGameSettings(); // DELETEME debug
    }

    public void enableGameSettings() {
        guiApplication.lookup("gameSettingsPane").setDisable(false);
        guiApplication.lookup("towerWizardPane").setDisable(true);
        ( (ChoiceBox<String>) guiApplication.lookup("numChoice") ).getSelectionModel().select(0);
        ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice") ).getSelectionModel().select(0);
        guiApplication.lookup("gameSettingsButton").requestFocus();
    }

    public void sendGameSettings() {
        // if something's missing: do nothing
        // sending stuff to client; if successful
        guiApplication.lookup("gameSettingsPane").setDisable(true);
        guiApplication.lookup("towerWizardPane").setDisable(false);
        guiApplication.lookup("towerWizardButton").requestFocus();
        updateTowerWizard(); // DELETEME debug
    }

    public void updateTowerWizard() {
        // called when there's an update in the game and I'm still waiting for it to start
        // ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(updatedGame.getTowerColors());
        // or something like that. and then set defaults:
        ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getSelectionModel().select(0);
        ( (ChoiceBox<String>) guiApplication.lookup("wizardChoice") ).getSelectionModel().select(0);
    }

    public void sendTowerColor() {
        // TowerColor lookup.gettext e switch case per trasformare in enum
        // fallisce se il campo è vuoto
        //towercolor disable
        System.out.println("Tower color chosen"); // DELETEME debug
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
        for (int i = 0; i < 12; i++) {
            List<Integer> newGroup = new ArrayList<>();
            newGroup.add(i);
            groupList.add(newGroup);
        }
        updateArchipelago(); //DELETEME debug
    }

    public void updateArchipelago() {
        // compare old groupList with new groupList, in the island tiles number order
        // when a diff is found in island j which has just been merged:
        // ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
        // Point2D mergeDiff = archipelagoPane.calcMergeDiff(j - 1, j);
        // archipelagoPane.relocateBack(j, mergeDiff);
        // for i in (islandGroup che sto analizzando adesso, quello che contiene j-1) :
        // archipelagoPane.relocateForward(i, mergeDiff);

        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane"); // DELETEME debug tutta questa sezione
        Point2D mergeDiff = archipelagoPane.calcMergeDiff(0, 1);
        archipelagoPane.relocateBack(1, mergeDiff);
        archipelagoPane.relocateForward(0, mergeDiff);
    }

}
