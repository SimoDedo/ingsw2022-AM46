package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.View.GUI.Application.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/**
 * Class that offers methods to modify and query the GUI (enabling/disabling, getting and updating).
 */
public class GUIController implements ObserverGUI {

    /**
     * The GUI associated with this controller.
     */
    private final GUI gui;
    /**
     * The GUIApplication actually controlled by this class.
     */
    private final GUIApplication guiApplication;
    /**
     * The nickname of the player associated with this GUI.
     */
    private String nickname;
    /**
     * Map to assign a numeric ID to each player.
     */
    private HashMap<String, Integer> nickMap;

    private final boolean useBridges = false;

    /**
     * The next user action that is expected of the player in order to progress the game.
     * Used to save the current state and retrieved to return to that state once an ability was used.
     */
    private UserActionType nextUserAction;

    /**
     * True if an ability was requested and is now being used.
     */
    private boolean characterAbilityState = false;
    /**
     * The parameters requested by the active character.
     */
    private List<RequestParameter> requestParameters;
    /**
     * The parameters selected to use the activated character's ability.
     */
    private final List<Integer> characterParameters;

    /**
     * The constructor for the GUIController
     * @param gui the GUI associated with this controller.
     */
    public GUIController(GUI gui) {
        this.gui = gui;
        guiApplication = GUIApplication.getInstance();
        guiApplication.setObserver(this);
        this.characterParameters = new ArrayList<>();
    }

    /**
     * Sets the nickname of the player associated with this GUI.
     * @param nickname the nickname of the player associated with this GUI.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Displays an error that occurred to the user though an alert box.
     * @param errorDescription the error to display.
     * @param isGameEnding true if said error causes the game to stop.
     */
    public void displayError(String errorDescription, boolean isGameEnding) {
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            Stage stage = (Stage) errorDialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            errorDialog.setTitle(isGameEnding ?"Fatal error" : "Error");
            errorDialog.setHeaderText(isGameEnding ? "An error has occurred!" : "Wrong action!");
            errorDialog.setContentText(errorDescription +(isGameEnding ? "" : " Please choose another move!"));
            errorDialog.showAndWait();
            if(isGameEnding)
                endGame();
        });
    }

    /**
     * Displays given information to the user though an alert box.
     * @param info the information to be displayed.
     */
    public void displayInfo(String info) {
        GUIApplication.runLaterExecutor.execute(() -> {
            if (guiApplication.lookup("log") != null)
                ((Log) guiApplication.lookup("log")).push(info);
        });
    }

    @Override
    public void notifyClose() {
        gui.close();
    }

    /**
     * Switches the current scene to the login scene.
     */
    public void switchToLogin(){
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToLogin);
    }

    @Override
    public void notifyIP() {
        gui.notifySetupInput();
    }

    /**
     * Returns the IP chosen in the GUI.
     * @return the IP chosen in the GUI.
     */
    public Map<String, String> getIPChosen(){
        Map<String, String> map = new HashMap<>();
        map.put("IP",( (TextField) guiApplication.lookup("ipField")).getText() );
        map.put("port" ,  ( (TextField) guiApplication.lookup("portField")).getText());
        return map;
    }

    /**
     * Method called when the connection to the server was successful. Enables the nickname pane.
     */
    public void connectToIPSuccessful() {
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("ipPane").setDisable(true));
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("nickPane").setDisable(false));
        GUIApplication.runLaterExecutor.execute(() -> ( (Button) guiApplication.lookup("connectButton")).setText("Connected successfully!"));
    }

    @Override
    public void notifyNickname() {
        gui.notifySetupInput();
    }

    /**
     * Returns the nickname chosen in the GUI.
     * @return the nickname chosen in the GUI.
     */
    public String getNicknameChosen(){
        return ( (TextField) guiApplication.lookup("nickField")).getText();
    }

    /**
     * Method called when the login with nickname was successful. Switches the scene to the game setup scene.
     */
    public void connectWithNicknameSuccessful() {
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToGameSetup);
    }

    /**
     * Enables the selection of the game settings on the game setup scene.
     */
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

    @Override
    public void notifyGameSettings() {
        gui.notifySetupInput();
    }

    /**
     * Returns the number of players chosen in the GUI.
     * @return the number of players chosen in the GUI.
     */
    public int getNumOfPlayerChosen(){
        String numChoice = ( (ChoiceBox<String>) guiApplication.lookup("numChoice")).getValue();
        return Integer.parseInt(numChoice);
    }


    /**
     * Returns the game mode chosen in the GUI.
     * @return the game mode chosen in the GUI.
     */
    public GameMode getGameModeChosen(){
        String gameMode = ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice")).getValue();
        if("STANDARD".equalsIgnoreCase(gameMode))
            gameMode = "NORMAL";
        return GameMode.valueOf(gameMode.toUpperCase());
    }

    /**
     * Shows the game mode chosen for this game.
     */
    public void showGameMode(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ( (ChoiceBox<String>) guiApplication.lookup("numChoice")).setValue(String.valueOf(game.getNumOfPlayers()));
            String mode = game.getGameMode() == GameMode.NORMAL ? "Standard" : "Expert"; //This sucks but __someone__ changed it from normal to standard.
            ( (ChoiceBox<String>) guiApplication.lookup("gameModeChoice")).setValue(mode);
        });
    }

    /**
     * Enables the selection of a tower color and a wizard type.
     */
    public void enableTowerWizard() {
        GUIApplication.runLaterExecutor.execute(() -> {
            guiApplication.lookup("gameSettingsPane").setDisable(true);
            guiApplication.lookup("towerWizardPane").setDisable(false);
            guiApplication.lookup("towerWizardButton").requestFocus();
        });
    }

    /**
     * Updates the list of available tower colors and wizards.
     * @param towerColors the available tower colors.
     * @param wizards the available wizards.
     */
    public void updateTowerWizard(List<TowerColor> towerColors, List<WizardType> wizards){
        // called when there's an update in the game and the server is still waiting for it to start
        // ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getItems().setAll(updatedGame.getTowerColors());
        // or something like that. and then set defaults:
        List<String> tc = towerColors.stream()
                .map(t -> t.toString().charAt(0) + t.toString().substring(1).toLowerCase())
                .toList();
        List<String> wiz = wizards.stream()
                .map(w -> w.toString().charAt(0) + w.toString().substring(1).toLowerCase())
                .toList();
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

    /**
     * Method called when the tower color choice was successful. disables the pane.
     */
    public void towerColorSuccessful(){
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("colorChoice").setDisable(true));
    }

    @Override
    public void notifyTowerColor() {
        gui.notifySetupInput();
    }

    /**
     * Returns the tower color chosen in the GUI.
     * @return the tower color chosen in the GUI.
     */
    public TowerColor getTowerColorChosen(){
        String choice = ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getValue();
        return TowerColor.valueOf(choice == null ? null : choice.toUpperCase());
    }

    @Override
    public void notifyWizardType() {
        gui.notifySetupInput();
    }

    /**
     * Returns the wizard type chosen in the GUI.
     * @return the wizard type chosen in the GUI.
     */
    public WizardType getWizardChosen(){
        String choice = ( (ChoiceBox<String>) guiApplication.lookup("wizardChoice") ).getValue();
        return WizardType.valueOf(choice.toUpperCase());
    }

    /**
     * Disables the tower and wizard panes and displays a message to signal that user has to wait for others to start.
     */
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Starts the game by switching the scene to the main scene.
     */
    public void startGame() {
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToMain);
    }

    /**
     * Draws all game components on the GUI.
     * @param game The ObservableByClient which holds all data about the current game.
     * @param nickname The player associated with this GUI.
     */
    public void initialDraw(ObservableByClient game, String nickname) {
        nickMap = new HashMap<>();
        for(String nick : game.getPlayers())
            nickMap.put(nick, game.getPlayers().indexOf(nick));
        GUIApplication.runLaterExecutor.execute(() -> {
            //Draws players
            guiApplication.createPlayer(game.getGameMode(), nickname, nickMap.get(nickname),game.getEntranceID(nickname),
                    game.getTableIDs(nickname), game.getPlayerTeams().get(nickname),
                    game.getPlayersWizardType().get(nickname),true);
            for(String other : game.getPlayers()){
                if(! other.equals(nickname)){
                    guiApplication.createPlayer(game.getGameMode(), other, nickMap.get(other),game.getEntranceID(other),
                            game.getTableIDs(other), game.getPlayerTeams().get(other),
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
                PlayerPane playerPane = (PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nick));
                playerPane.updateEntrance(game.getEntranceStudentsIDs(nick));
                playerPane.updateTowers(game.getTowersLeft(nick));
                if(game.getGameMode() == GameMode.EXPERT)
                    playerPane.updateCoins(game.getCoins(nick));
            }
            ((TurnOrderPane)guiApplication.lookup("turnOrderPane")).updateTurnOrderPane(game.getCurrentPhase(), game.getCurrentPlayer(), game.getPlayerOrder());

            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.updateIslandStudents(game.getIslandTilesStudentsIDs(), game.getArchipelagoStudentIDs());
            archipelagoPane.updateBag(game.getBagStudentsLeft());
            if(game.getGameMode() == GameMode.EXPERT){
                archipelagoPane.updateCoinHeap(game.getCoinsLeft());
                for(Integer charID : game.getDrawnCharacterIDs())
                    archipelagoPane.updateCharacter(charID, false, 0, game.getCharacterStudents(charID),
                            game.getNoEntryTilesCharacter(charID), game.getCharacterOvercharge(charID));
            }
            for(Integer cloud : game.getCloudIDs()){
                archipelagoPane.updateCloud(cloud, game.getCloudStudentsIDs(cloud));
            }
        });
    }

    /**
     * Updates the turn order banner.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updateTurnOrder(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> ((TurnOrderPane)guiApplication.lookup("turnOrderPane"))
                .updateTurnOrderPane(game.getCurrentPhase(), game.getCurrentPlayer(), game.getPlayerOrder()));
    }

    /**
     * Updates the assistants in a player's hand.
     * @param player The player to update.
     * @param assistantUsed The assistant currently played.
     * @param assistantsLeft The assistants left in his hand.
     */
    public void updateAssistants(String player, Integer assistantUsed, List<Integer> assistantsLeft){
        GUIApplication.runLaterExecutor.execute(() -> ((PlayerPane)guiApplication.lookup("playerPane" + nickMap.get(player)))
                .updateAssistants(assistantUsed, assistantsLeft));
    }

    /**
     * Updates the board of each player.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updatePlayerBoards(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            for(String player : game.getPlayers()){
                PlayerPane playerPane = (PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(player));
                playerPane.updateEntrance(game.getEntranceStudentsIDs(player));
                for(Color color : Color.values())
                    playerPane.updateDiningRoom(color, game.getTableStudentsIDs(player, color));
                playerPane.updateProfessors(game.getProfessorsOwner());
                playerPane.updateTowers(game.getTowersLeft(player));
                if(game.getGameMode() == GameMode.EXPERT)
                    playerPane.updateCoins(game.getCoins(player));
            }
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.updateBag(game.getBagStudentsLeft());
        });
    }

    /**
     * Updates the clouds.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updateCloud(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            for(Integer cloudID : game.getCloudIDs()){
                archipelagoPane.updateCloud(cloudID, game.getCloudStudentsIDs(cloudID));
            }
            archipelagoPane.updateBag(game.getBagStudentsLeft());
        });
    }

    /**
     * Updates all info related to the archipelago and its islands.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updateArchipelago(ObservableByClient game) {
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");

            List<Integer> islandIDs = new ArrayList<>();
            for (Integer group : game.getIslandTilesIDs().keySet())
                islandIDs.addAll(game.getIslandTilesIDs().get(group));
            archipelagoPane.updateMotherNature(game.getMotherNatureIslandTileID(), islandIDs);
            archipelagoPane.updateMovePower(game.getActualMovePower(nickname), game.getMotherNatureIslandGroupIdx());
            archipelagoPane.updateIslandStudents(game.getIslandTilesStudentsIDs(), game.getArchipelagoStudentIDs());

            HashMap<Integer,TowerColor> islandTowers = new HashMap<>();
            for (Integer group : game.getIslandGroupsOwners().keySet()){
                for(Integer islandID : game.getIslandTilesIDs().get(group)){
                    islandTowers.put(islandID, game.getIslandGroupsOwners().get(group));
                }
            }
            archipelagoPane.updateTowers(islandTowers);
            archipelagoPane.updateBag(game.getBagStudentsLeft());

            HashMap<Integer,Integer> islandNoEntry = new HashMap<>();
            for (Integer group : game.getIslandGroupsOwners().keySet()){
                for(Integer islandID : game.getIslandTilesIDs().get(group)){
                    if(game.getIslandTilesIDs().get(group).indexOf(islandID) == 0)
                        islandNoEntry.put(islandID, game.getNoEntryTilesArchipelago().get(group));
                    else
                        islandNoEntry.put(islandID, 0);
                }
            }
            archipelagoPane.updateNoEntry(islandNoEntry);

            archipelagoPane.updateMerge(game.getIslandTilesIDs());
        });
    }

    /**
     * Updates the character state and contents.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updateCharacters(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            for(Integer charID : game.getDrawnCharacterIDs())
                archipelagoPane.updateCharacter(charID, game.getActiveCharacterID() == charID, game.getActiveCharacterUsesLeft(),
                        game.getCharacterStudents(charID), game.getNoEntryTilesCharacter(charID), game.getCharacterOvercharge(charID));
            archipelagoPane.updateCoinHeap(game.getCoinsLeft());
        });
    }

    /**
     * Updates the parameters requested by the character.
     * @param game The ObservableByClient which holds all data about the current game.
     */
    public void updateCharacterRequest(ObservableByClient game){
        this.requestParameters = game.getCurrentRequestParameters();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Enables this player's assistant to be played.
     */
    public void enableAssistants() {
        nextUserAction = UserActionType.PLAY_ASSISTANT;
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectAssistant();
        });
    }

    /**
     * Disables this player's assistants.
     */
    public void disableAssistants() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectAssistant();
        });
    }

    /**
     * Enables students in this player's entrance to be selected.
     */
    public void enableEntrance(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsEntrance();
        });
    }
    /**
     * Enables students in this player's entrance to be selected, while specifying the next action (i.e. the action
     * that requires the student to be selected).
     * @param nextUserAction the next action to be taken.
     */
    public void enableEntrance(UserActionType nextUserAction){
        this.nextUserAction = nextUserAction;
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsEntrance();
        });
    }

    /**
     * Disables students in this player's entrance.
     */
    public void disableEntrance(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectStudentsEntrance();
        });
    }

    /**
     * Enables students in this player's dining room to be selected.
     */
    public void enableDRStudents(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsDR();
        });
    }

    /**
     * Disables students in this player's dining room.
     */
    public void disableDRStudents(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectStudentsDR();
        });
    }

    /**
     * Enables all islands to be selected.
     */
    public void enableIslands() {
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.enableSelectIsland();
        });
    }

    /**
     * Enables only islands that can actually be selected, while specifying the next action (i.e. the action
     * that requires the island to be selected).
     * @param nextUserAction the next action to be taken.
     */
    public void enableIslands(UserActionType nextUserAction) {
        this.nextUserAction = nextUserAction;
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            if(nextUserAction == UserActionType.MOVE_MOTHER_NATURE)
                archipelagoPane.enableSelectIslandReachable();
            else
                archipelagoPane.enableSelectIsland();
        });
    }

    /**
     * Disables all islands.
     */
    public void disableIslands() {
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.disableSelectIsland();
        });
    }

    /**
     * Enables clouds to be selected, while implicitly specifying the next action (i.e. the action
     * that requires the cloud to be selected). It is always the take from cloud user action.
     */
    public void enableClouds() {
        nextUserAction = UserActionType.TAKE_FROM_CLOUD;
        GUIApplication.runLaterExecutor.execute(() -> {
            CloudContainerPane cloudContainerPane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
            cloudContainerPane.enableSelectCloud();
        });
    }

    /**
     * Disables clouds.
     */
    public void disableClouds() {
        GUIApplication.runLaterExecutor.execute(() -> {
            CloudContainerPane cloudContainerPane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
            cloudContainerPane.disableSelectCloud();
        });
    }

    /**
     * Enables all of this player's tables to be selected.
     */
    public void enableTables() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectTables();
        });
    }

    /**
     * Enables this player's table of the specified color to be selected.
     * @param color The color of the table to enable.
     */
    public void enableTables(Color color) {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectTables(color);
        });
    }

    /**
     * Disables all of this player's table.
     */
    public void disableTables() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectTables();
        });
    }

    /**
     * Enables all characters to be selected.
     */
    public void enableCharacters(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectCharacter();
        });
    }

    /**
     * Disables all characters.
     */
    public void disableCharacters(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectCharacter();
        });
    }

    /**
     * Enables the active character to be selected to activate its ability.
     */
    public void enableCharacterAbility(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableActivateCharacter();
        });
    }

    /**
     * Disables the active character.
     */
    public void disableCharacterAbility(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableActivateCharacter();
        });
    }

    /**
     * Enables students on the active character to be selected.
     */
    public void enableStudentChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectStudents();
        });
    }

    /**
     * Disables students on the active character.
     */
    public void disableStudentChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectStudents();
        });
    }

    /**
     * Enables the color selection panel to let a color be selected.
     */
    public void enableColorChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectColor();
        });
    }

    /**
     * Disables the color selection panel.
     */
    public void disableColorChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectColor();
        });
    }

    /**
     * Enables the end turn button to be clicked.
     */
    public void enableEndTurn(){
        nextUserAction = UserActionType.END_TURN;
        GUIApplication.runLaterExecutor.execute(() -> {
            Button endTurn = (Button) guiApplication.lookup("endButton");
            endTurn.setVisible(true);
        });
    }

    /**
     * Disables the end turn button.
     */
    public void disableEndTurn(){
        GUIApplication.runLaterExecutor.execute(() -> {
            Button endTurn = (Button) guiApplication.lookup("endButton");
            endTurn.setVisible(false);
        });
    }

    /**
     * Enables the last action that was active before activating a character ability. It uses the next user action
     * saved to recover the previous state.
     */
    public void enableLast(){
        if(nextUserAction != null){
            switch (nextUserAction){
                case MOVE_STUDENT -> enableEntrance(UserActionType.MOVE_STUDENT);
                case MOVE_MOTHER_NATURE -> enableIslands(UserActionType.MOVE_MOTHER_NATURE);
                case TAKE_FROM_CLOUD -> enableClouds();
                case END_TURN -> enableEndTurn();
            }
        }
    }

    /**
     * Enables all elements that were temporarily disabled.
     * This method doesn't enable each element individually but uses a global enabler;
     * this means that only elements that were originally individually enabled will be actually selectable.
     */
    public void reEnableAll(){
        guiApplication.reEnableGlobal();
    }

    /**
     * Temporarily disables all elements.
     * This method doesn't disable each element individually but uses a global disabler;
     * this means that previously enabled elements are disabled at a global level, but when all
     * elements are globally enabled the previously enabled elements will be selectable.
     */
    public void disableAllTemporary(){
        guiApplication.disableGlobal();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void notifyAssistantCard() {
        if (nextUserAction == UserActionType.PLAY_ASSISTANT)
            gui.sendSelection(new PlayAssistantUserAction(nickname, getAssistantCardChosen()));
        else
            System.out.println("Error in notifyAssistantCard: wrong user action");
    }

    /**
     * Retrieves the assistant card chosen by the player by querying the GUI.
     * @return The assistant card chosen.
     */
    public int getAssistantCardChosen() {
        AssistantContainerPane pane = (AssistantContainerPane) guiApplication.lookup("assistantContainerPane" + nickMap.get(nickname));
        return pane.getAssistantChosen();
    }

    @Override
    public void notifyStudentEntrance(){
        if (!characterAbilityState) {
            disableEntrance();
            enableIslands();
            enableTables(Color.valueOf(((StudentView)guiApplication.lookup("student"+ getStudentBoard())).getColor().toUpperCase()));
        }
        else {
            characterParameters.add(getStudentBoard());
            requestParameters.remove(0);
            parseNextRequestParameter();
        }
    }

    @Override
    public void notifyStudentDR(){
        if (characterAbilityState) {
            characterParameters.add(getStudentBoard());
            requestParameters.remove(0);
            parseNextRequestParameter();
        }
    }

    /**
     * Retrieves the student chosen by the player in the board by querying the GUI.
     * @return The student chosen in the board.
     */
    public int getStudentBoard(){
        BoardPane board = ((BoardPane) guiApplication.lookup("boardPane" + nickMap.get(nickname)));
        return board.getStudentChosen();
    }

    @Override
    public void notifyStudentChar(){
        if (characterAbilityState) {
            characterParameters.add(getStudentChar());
            requestParameters.remove(0);
            parseNextRequestParameter();
        }
    }

    /**
     * Retrieves the student chosen by the player in the active character by querying the GUI.
     * @return The student chosen by the player in the active character.
     */
    public int getStudentChar() {
        CharContainerPane chars = (CharContainerPane) guiApplication.lookup("charContainerPane");
        return chars.getStudentChosen();
    }

    @Override
    public void notifyCloud() {
        nextUserAction = null;
        gui.sendSelection(new TakeFromCloudUserAction(nickname, getCloudChosen()));
    }

    /**
     * Retrieves the cloud chosen by the player by querying the GUI.
     * @return The cloud chosen by the player.
     */
    public int getCloudChosen() {
        CloudContainerPane pane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
        return pane.getCloudChosen();
    }

    @Override
    public void notifyIsland() {
        if (!characterAbilityState) {
            if (nextUserAction == UserActionType.MOVE_STUDENT) {
                gui.sendSelection(new MoveStudentUserAction(nickname, getStudentBoard(), getIslandChosen()));
            }
            else if (nextUserAction == UserActionType.MOVE_MOTHER_NATURE) {
                gui.sendSelection(new MoveMotherNatureUserAction(nickname, getIslandChosen()));
            }
        } else {
            characterParameters.add(getIslandChosen());
            requestParameters.remove(0);
            parseNextRequestParameter();
        }
    }

    /**
     * Retrieves the island chosen by the player by querying the GUI.
     * @return The island chosen by the player.
     */
    public int getIslandChosen() {
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
        return archipelagoPane.getIslandChosen();
    }

    @Override
    public void notifyCharacter() {
        gui.sendSelection(new UseCharacterUserAction(nickname, getCharacterChosen()));
    }

    /**
     * Retrieves the character chosen by the player by querying the GUI.
     * @return The character chosen by the player.
     */
    public int getCharacterChosen() {
        CharContainerPane pane = (CharContainerPane) guiApplication.lookup("charContainerPane");
        return pane.getCharacterChosen();
    }

    @Override
    public void notifyTable() {
        if (!characterAbilityState) {
            gui.sendSelection(new MoveStudentUserAction(nickname, getStudentBoard(), getTableChosen()));
        } else
            System.out.println("Error in notifyTable: character ability already true");
    }

    /**
     * Retrieves the table chosen by the player by querying the GUI.
     * @return The table chosen by the player.
     */
    public int getTableChosen() {
        BoardPane boardPane = (BoardPane) guiApplication.lookup("boardPane" + nickMap.get(nickname));
        return boardPane.getTableChosen();
    }

    @Override
    public void notifyColorChar() {
        if (characterAbilityState) {
            characterParameters.add(getColorChar());
            requestParameters.remove(0);
            parseNextRequestParameter();
        }
    }

    /**
     * Retrieves the color chosen by the player in the active character by querying the GUI.
     * @return The color chosen by the player in the active character.
     */
    public int getColorChar() {
        CharContainerPane pane = (CharContainerPane) guiApplication.lookup("charContainerPane");
        CharacterPane character = (CharacterPane) pane.lookup("#characterPane" + pane.getCharacterChosen());
        return Arrays.stream(Color.values()).toList().indexOf(character.getColorChosen());
    }

    @Override
    public void notifyAbility() {
        characterAbilityState = true;
        parseNextRequestParameter();
    }

    /**
     * Parses the next request parameters, if present, by disabling everything an enabling only the elements
     * needed to satisfy the next parameter.
     * If no parameter is left, all the parameters collected so far get sent, then they are reset.
     */
    private void parseNextRequestParameter(){
        disableCharacterAbility();
        disableClouds();
        disableIslands();
        disableTables();
        disableEntrance();
        disableStudentChar();
        disableDRStudents();
        disableColorChar();
        disableEndTurn();
        if (requestParameters.size() != 0) {
            switch (requestParameters.get(0)) {
                case STUDENT_ENTRANCE -> enableEntrance();
                case ISLAND -> enableIslands();
                case STUDENT_DINING_ROOM -> enableDRStudents();
                case STUDENT_CARD -> enableStudentChar();
                case COLOR -> enableColorChar();
            }
        } else {
            characterAbilityState = false;
            gui.sendSelection(new UseAbilityUserAction(nickname, characterParameters));
            characterParameters.clear();
        }
    }

    @Override
    public void notifyEndTurn() {
        nextUserAction = null;
        gui.sendSelection(new EndTurnUserAction(nickname));
    }

    /**
     * Displays the winners of the game through an alert.
     * @param winner The winning team.
     * @param winners The winning players.
     * @param losers The losing players.
     */
    public void displayWinners(TowerColor winner, List<String> winners, List<String> losers){
        String title;
        StringBuilder toPrint = new StringBuilder();
        if(winners.contains(nickname)){
            title = "Winner!";
            toPrint.append("CONGRATULATIONS ");
            for(String player : winners){
                toPrint.append(player).append(" ");
            }
            toPrint.append("!! Team ").append(winner).append(" has WON!!!");
        }
        else {
            title = "Loser!";
            toPrint.append("Too bad! ");
            for(String player : losers){
                toPrint.append(player).append(" ");
            }
            toPrint.append("you lost! Team ").append(winner).append(" has won.");
        }
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert winnerDialogue = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage) winnerDialogue.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            winnerDialogue.setTitle(title);
            winnerDialogue.setHeaderText("Game ended!");
            winnerDialogue.setContentText(toPrint.toString());
            winnerDialogue.showAndWait();
            endGame();
        });
    }

    /**
     * Shows an alert then ends the game and resets the GUI and client.
     */
    private void endGame(){
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert endGameDialogue = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage) endGameDialogue.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            endGameDialogue.setTitle("Game ended");
            endGameDialogue.setHeaderText("The game has ended!");
            endGameDialogue.setContentText("You will now be redirected to the login screen.");
            endGameDialogue.showAndWait();
            guiApplication.createLoginScene();
            guiApplication.createGameSetupScene();
            guiApplication.createMainScene();
            gui.reset();
        });
    }

}
