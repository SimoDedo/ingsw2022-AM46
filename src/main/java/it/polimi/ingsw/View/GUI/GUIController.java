package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.GUI.Application.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for controlling the GUIController based on method calls from the client.
 */
public class GUIController {

    GUI gui;
    private final GUIApplication guiApplication;

    private List<List<Integer>> groupList = new ArrayList<>();

    private boolean debug = true;

    private boolean useBridges = false;

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

    public void close() {
        gui.close();
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
        if (debug) {
            enableGameSettings(); // DELETEME debug
        }
    }

    public void enableGameSettings() {
        GUIApplication.runLaterExecutor.execute(() -> {
            VBox root = (VBox) guiApplication.lookup("gameSetupRoot");
            root.setBackground(new Background(new BackgroundImage(
                    new Image("/general/bg2_unfocused.png"),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
            )));
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
            showTowerWizard(); // DELETEME debug
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
        if("STANDARD".equalsIgnoreCase(gameMode))
            gameMode = "NORMAL";
        return GameMode.valueOf(gameMode.toUpperCase());
    }

    public void showGameMode(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ( (ChoiceBox<String>) guiApplication.lookup("numChoice")).setValue(String.valueOf(game.getNumOfPlayers()));
            String mode = game.getGameMode() == GameMode.NORMAL ? "Standard" : "Expert"; //This sucks but __someone__ changed it from normal to standard.
            ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice")).setValue(mode);
        });
    }

    public void showTowerWizard() {
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.lookup("gameSettingsPane").setDisable(true);
            guiApplication.lookup("towerWizardPane").setDisable(false);
            guiApplication.lookup("towerWizardButton").requestFocus();

            if(debug){
                ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getSelectionModel().select(0);
                ( (ChoiceBox<String>) guiApplication.lookup("wizardChoice") ).getSelectionModel().select(0);
            }
        });
    }

    public void updateTowerWizard(List<TowerColor> towerColors, List<WizardType> wizards){
        // called when there's an update in the game and I'm still waiting for it to start
        // ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(updatedGame.getTowerColors());
        // or something like that. and then set defaults:
        List<String> tc = towerColors.stream()
                .map(t -> t.toString().charAt(0) + t.toString().substring(1).toLowerCase())
                .toList();
        List<String> wiz = wizards.stream()
                .map(w -> w.toString().charAt(0) + w.toString().substring(1).toLowerCase())
                .toList();
        if(! guiApplication.lookup("towerWizardPane").isDisable()){
            GUIApplication.runLaterExecutor.execute(() -> {
                if(! guiApplication.lookup("colorChoice").isDisable() ){
                    ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(tc);
                    ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getSelectionModel().select(0);
                }
                if(! guiApplication.lookup("wizardChoice").isDisable() ) {
                    ((ChoiceBox<String>) guiApplication.lookup("wizardChoice")).getItems().setAll(wiz);
                    ((ChoiceBox<String>) guiApplication.lookup("wizardChoice")).getSelectionModel().select(0);
                }
                guiApplication.lookup("towerWizardButton").requestFocus();
            });
        }
    }

    public void towerColorSuccessful(){
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.lookup("colorChoice").setDisable(true);
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
            guiApplication.lookup("towerWizardPane").setDisable(true);
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
            if (debug) { //DELETEME debug
                HashMap<Color, Integer> table = new HashMap<>();
                for (Color color : Color.values())
                    table.put(color, 0);
                for (int i = 0; i < 3; i++) {
                    guiApplication.createPlayer(GameMode.EXPERT,"Player" + i,0 , table, TowerColor.WHITE, 8, WizardType.KING,i == 0);
                    ((BoardPane) guiApplication.lookup("boardPane"+"Player" + i)).debugPawn();
                }
                ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
                guiApplication.createArchipelago(3, GameMode.EXPERT, List.of(0,1,2,3,4,5,6,7,8,9,10,11),
                        List.of(0,1,2,3), List.of(2,5,6), 5);
                archipelagoPane.debugStud();

                CloudContainerPane cloudContainerPane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
                cloudContainerPane.enableSelectCloud();
                CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
                charContainerPane.enableSelectCharacter();

                ((PlayerPane) guiApplication.lookup("playerPanePlayer0")).enableSelectAssistant();
            }
        });
    }

    public void initialDraw(ObservableByClient game, String nickname){ //THIS WILL GET DIVIDED IN MORE METHOD which will be reused by GUI
        GUIApplication.runLaterExecutor.execute(() -> {
            //Draws players
            guiApplication.createPlayer(game.getGameMode(), nickname, game.getEntranceID(nickname),
                    game.getTableIDs(nickname), game.getPlayerTeams().get(nickname), game.getTowersLeft(nickname),
                    game.getPlayersWizardType().get(nickname),true);
            for(String other : game.getPlayers()){
                if(! other.equals(nickname)){
                    guiApplication.createPlayer(game.getGameMode(), other, game.getEntranceID(other),
                            game.getTableIDs(other), game.getPlayerTeams().get(other), game.getTowersLeft(other),
                            game.getPlayersWizardType().get(other), false);
                }
            }
            //Draws archipelago, clouds and characters
            List<Integer> islandIDs = new ArrayList<>();
            for (int j = 0; j < game.getIslandTilesIDs().size(); j++) {
                islandIDs.addAll(game.getIslandTilesIDs().get(j));
            }
            guiApplication.createArchipelago(game.getNumOfPlayers(), game.getGameMode(), islandIDs, game.getCloudIDs(),
                    game.getDrawnCharacterIDs(), game.getMotherNatureIslandTileID());

            for(String nick : game.getPlayers()){
                PlayerPane playerPane = (PlayerPane) guiApplication.lookup("playerPane" + nick);
                playerPane.updateEntrance(game.getEntranceStudentsIDs(nick));
                playerPane.updateTowers(game.getTowersLeft(nick));
                if(game.getGameMode() == GameMode.EXPERT)
                    playerPane.updateCoins(game.getCoins(nick));
            }
            ((TurnOrderPane)guiApplication.lookup("turnOrderPane")).updateTurnOrderPane(game.getCurrentPhase(), game.getCurrentPlayer(), game.getPlayerOrder());

            ArchipelagoPane archipelagoPane = (ArchipelagoPane)guiApplication.lookup("archipelagoPane");
            archipelagoPane.updateIslandStudents(game.getIslandTilesStudentsIDs(), game.getArchipelagoStudentIDs());
            archipelagoPane.updateBag(game.getBagStudentsLeft());
            if(game.getGameMode() == GameMode.EXPERT){
                archipelagoPane.updateCoinHeap(game.getCoinsLeft());
                for(Integer charID : game.getDrawnCharacterIDs())
                    archipelagoPane.updateCharacter(charID , game.getCharacterStudents(charID), game.getNoEntryTilesCharacter(charID));
            }
            for(Integer cloud : game.getCloudIDs()){
                archipelagoPane.updateCloud(cloud, game.getCloudStudentsIDs(cloud));
            }
        });
    }

    /**
     * Notifies gui that a card has been chosen. Used by assistant cards.
     */
    public void notifyAssistantCard() {
        if (debug) {
            System.out.println("Assistant card chosen");
            assistantCardSuccessful();
        }
        else {
            gui.notifyInput();
        }
    }

    /**
     * Finds the player's assistant card pane and retrieves the ID of the selected card. Used by gui.
     *
     */
    public int getAssistantCardChosen() {
        AssistantContainerPane pane = (AssistantContainerPane) guiApplication.lookup("assistantContainerPane0");
        System.out.println(pane.getAssistantChosen()); // DELETEME debug
        return pane.getAssistantChosen();
    }

    /**
     * Effectively moves the card to the discard pile. Used by gui.
     */
    public void assistantCardSuccessful() {
        PlayerPane playerPane = (PlayerPane) guiApplication.lookup("playerPanePlayer0");
        AssistantContainerPane assistantPane = (AssistantContainerPane) guiApplication.lookup("assistantContainerPanePlayer0");
        playerPane.moveAssistant(assistantPane.getAssistantChosen());
    }

    public void notifyCloud() {
        if (debug) {
            System.out.println("Cloud chosen");
            cloudSuccessful();
        }
        else {
            gui.notifyInput();
        }
    }

    public int getCloudChosen() {
        CloudContainerPane pane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
        System.out.println(pane.getCloudChosen()); // DELETEME debug
        return pane.getCloudChosen();
    }

    public void cloudSuccessful() {
        CloudContainerPane pane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
        pane.emptyCloud(pane.getCloudChosen());
    }

    public void notifyCharacter() {
        if (debug) {
            System.out.println("Character chosen");
            characterSuccessful();
        }
        else {
            gui.notifyInput();
        }
    }

    public int getCharacterChosen() {
        CharContainerPane pane = (CharContainerPane) guiApplication.lookup("charContainerPane");
        System.out.println(pane.getCharacterChosen()); // DELETEME debug
        return pane.getCharacterChosen();
    }

    public void characterSuccessful() {
        CharContainerPane pane = (CharContainerPane) guiApplication.lookup("charContainerPane");
        // wip: what to do afterwards?

    }

    public void updateArchipelago() {

        /* version with movement:
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

        /* version with bridges:
        bridges only appear when an island is conquered => when an islandgroup changes size
        we only check new and old group sizes, back to back
        if the size is the same, nothing has changed. go to the next group
        otherwise,
        call setBridge(i, tower color) on every tile index i inside the group (redundant if the color hasn't changed and
        there's only been an addition to the group, but whatever since this single line encompasses both addition and
        re-conquest
        */

        //example code:

        /*ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");// DELETEME debug tutta questa sezione
        if (useBridges) {
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(0, TowerColor.WHITE));
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(2, TowerColor.GREY));
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.setBridge(4, TowerColor.BLACK));
        }
        else {
            final Point2D mergeDiff = archipelagoPane.calcMergeDiff(2, 3);
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(3, mergeDiff));
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(2, mergeDiff));


            final Point2D mergeDiff2 = archipelagoPane.calcMergeDiff(4, 5);
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(5, mergeDiff2));
            GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(4, mergeDiff2));
        }*/
    }

    public void debugFunction1() {
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane"); // DELETEME debug tutta questa sezione

        final Point2D secondMergeDiff = archipelagoPane.calcMergeDiff(3, 4);
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(4, secondMergeDiff));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateBack(5, secondMergeDiff));

        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(2, secondMergeDiff));
        GUIApplication.runLaterExecutor.execute(() -> archipelagoPane.relocateForward(3, secondMergeDiff));
    }
}
