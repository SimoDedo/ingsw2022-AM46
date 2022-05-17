package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.View.Client;
import it.polimi.ingsw.View.UI;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

/**
 * Class for controlling the GUI based on method calls from the client.
 */
public class GUI implements UI {

    Client client;
    private GUIApplication guiApplication;

    public GUI(Client client) {
        this.client = client;
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
        enableGameSettings(); // DEBUGGGGGGGGGGGGG

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

    //startregion cagganculo

    @Override
    public void setNickname(String nickname) {

    }


    @Override
    public void update(ObservableByClient game) {

    }

    public void startGame() {
        guiApplication.switchToMain();
    }

    @Override
    public void updateCommands(List<Command> toDisable, List<Command> toEnable) {

    }

    @Override
    public void notifyServerResponse(boolean gameStarted) {

    }

    @Override
    public Map<String, String> requestServerInfo(String defaultIP, int defaultPort) {
        return null;
    }

    @Override
    public String requestNickname() {
        return null;
    }

    @Override
    public void requestGameSettings() {

    }

    @Override
    public void requestTowerColor(ObservableByClient game) {

    }

    @Override
    public void requestWizard(ObservableByClient game) {

    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void displayInfo(String info) {

    }

    @Override
    public void displayError(String error, boolean isUrgent) {

    }

    @Override
    public void displayBoard(ObservableByClient game, UserActionType actionTaken) {

    }

    //endregion

}
