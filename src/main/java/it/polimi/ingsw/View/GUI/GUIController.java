package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
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

import java.util.*;

/**
 * Class for controlling the GUIController based on method calls from the client.
 */
public class GUIController {

    GUI gui;
    private GUIApplication guiApplication;

    private List<List<Integer>> groupList = new ArrayList<>();

    private boolean debug = false;

    public GUIController(GUI gui) {
        this.gui = gui;
        guiApplication = GUIApplication.getInstance();
        guiApplication.setController(this);
    }

    public void displayError(String errorDescription) {
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            Stage stage = (Stage) errorDialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText("Wrong action!");
            errorDialog.setContentText(errorDescription + ". Please choose another move or select Help > Game Rules to get further info!");
            errorDialog.showAndWait();
        });
    }

    public void connectToIP() {
        if(debug){
            String IP = ( (TextField) guiApplication.lookup("ipField")).getText();
            String port = ( (TextField) guiApplication.lookup("portField")).getText();
            System.out.println("Connecting to " + IP + ":" + port); // DELETEME debug
            connectToIPSuccessful(); // DELETEME debug
        }
        else {
            gui.notifyInput();
        }
    }

    public Map<String, String> getIPChosen(){
        Map<String, String> map = new HashMap<>();
        map.put("IP",( (TextField) guiApplication.lookup("ipField")).getText() );
        map.put("port" ,  ( (TextField) guiApplication.lookup("portField")).getText());
        return map;
    }

    public void connectToIPSuccessful() {
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("ipPane").setDisable(true));
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("nickPane").setDisable(false));
        GUIApplication.runLaterExecutor.execute(() -> ( (Button) guiApplication.lookup("connectButton")).setText("Connected successfully!"));
    }

    public void connectWithNickname() {
        if(debug){
            String nickname = ( (TextField) guiApplication.lookup("nickField")).getText();
            System.out.println("Connecting with nickname " + nickname); // DELETEME debug
            connectWithNicknameSuccessful(); // DELETEME debug
        }
        else {
            gui.notifyInput();
        }
    }

    public String getNicknameChosen(){
        return ( (TextField) guiApplication.lookup("nickField")).getText();
    }

    public void connectWithNicknameSuccessful() {
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.switchToGameSetup());
        if(debug){
            enableGameSettings(); // DELETEME debug
        }
    }

    public void enableGameSettings() {
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.lookup("gameSettingsPane").setDisable(false);
            guiApplication.lookup("towerWizardPane").setDisable(true);
            ( (ChoiceBox<String>) guiApplication.lookup("numChoice") ).getSelectionModel().select(0);
            ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice") ).getSelectionModel().select(0);
            guiApplication.lookup("gameSettingsButton").requestFocus();
        });
    }

    public void sendGameSettings() {
        if(debug){
            // if something's missing: do nothing
            // sending stuff to client; if successful
            guiApplication.lookup("gameSettingsPane").setDisable(true);
            guiApplication.lookup("towerWizardPane").setDisable(false);
            guiApplication.lookup("towerWizardButton").requestFocus();
            updateTowerWizard(); // DELETEME debug
        }
        else {
            gui.notifyInput();
        }
    }

    public int getNumOfPlayerChosen(){
        String numChoice = ( (ChoiceBox<String>) guiApplication.lookup("numChoice")).getValue();
        return Integer.parseInt(numChoice);
    }

    public GameMode getGameModeChosen(){
        String gameMode = ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice")).getValue();
        if("STANDARD".equals(gameMode.toUpperCase()))
            gameMode = "NORMAL";
        return GameMode.valueOf(gameMode.toUpperCase());
    }

    public void updateTowerWizard() {
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.lookup("gameSettingsPane").setDisable(true);
            guiApplication.lookup("towerWizardPane").setDisable(false);
            guiApplication.lookup("towerWizardButton").requestFocus();
            // called when there's an update in the game and I'm still waiting for it to start
            // ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(updatedGame.getTowerColors());
            // or something like that. and then set defaults:
            ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getSelectionModel().select(0);
            ( (ChoiceBox<String>) guiApplication.lookup("wizardChoice") ).getSelectionModel().select(0);
        });
    }

    public void sendTowerColor() {
        if(debug){
            // TowerColor lookup.gettext e switch case per trasformare in enum
            // fallisce se il campo è vuoto
            //towercolor disable
            System.out.println("Tower color chosen"); // DELETEME debug
        }
        else{
            gui.notifyInput();
        }
    }

    public TowerColor getTowerColorChosen(){
        String choice = ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getValue();
        return TowerColor.valueOf(choice.toUpperCase());
    }

    public void sendWizardType() {
        if(debug){
            // if tower color is null, don't do anything!!!!!!!!!
            System.out.println("Wizard type chosen");
            // wizard lookup.gettext e switch case per trasformare in enum
            // fallisce se il campo è vuoto
            waitForStart();
            startGame();
        }
        else {
            gui.notifyInput();
        }
    }

    public WizardType getWizardChosen(){
        String choice = ( (ChoiceBox<String>) guiApplication.lookup("wizardChoice") ).getValue();
        return WizardType.valueOf(choice.toUpperCase());
    }

    public void waitForStart(){
        GUIApplication.runLaterExecutor.execute(() -> {
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
        });
    }



    public void startGame() {
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.switchToMain();
            for (int i = 0; i < 12; i++) {
                List<Integer> newGroup = new ArrayList<>();
                newGroup.add(i);
                groupList.add(newGroup);
            }
            updateArchipelago(); //DELETEME debug
        });
    }

    public void updateArchipelago() {
        // version with movement:
        /*
         compare old groupList with new groupList, in the island tiles number order
         pick tile from old list
         pick group from new list
         group contains tile?
             if no-> go to next group in new list
             if yes-> old tile group and new tile group have same size?
                 if yes-> go to next tile in old list
                 if no-> tile has been merged in this group (either stuck to it, or was sandwiched between two groups that united)
                 goto algorithm below

         ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
         Point2D mergeDiff = archipelagoPane.calcMergeDiff(j - 1, j);
         archipelagoPane.relocateBack(j, mergeDiff);
         for i in (tiles contained in the same group as j - 1 AND less than j, so merge only works in one direction and is easier to predict) :
         archipelagoPane.relocateForward(i, mergeDiff);
        */

        // example code:
        /*
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane"); // DELETEME debug tutta questa sezione
        final Point2D mergeDiff = archipelagoPane.calcMergeDiff(0, 1);
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(1, mergeDiff));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(0, mergeDiff));*/

        // version with bridges:
        /*
        bridges only appear when an island is conquered => when an islandgroup changes size
        we only check new and old group sizes, back to back
        if the size is the same, nothing has changed. go to the next group
        otherwise,
        call setBridge(i, tower color) on every tile index i inside the group (redundant if the color hasn't changed and
        there's only been an addition to the group, but whatever since this single line encompasses both addition and
        re-conquest
         */
        //example code:
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(0, "white"));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(1, "grey"));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(2, "black"));

    }

    public void utilityFunction() {
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane"); // DELETEME debug tutta questa sezione

        final Point2D secondMergeDiff = archipelagoPane.calcMergeDiff(1, 2);
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(2, secondMergeDiff));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(1, secondMergeDiff));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(0, secondMergeDiff));
    }



}
