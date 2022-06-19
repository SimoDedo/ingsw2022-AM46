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
 * Class for controlling the GUIController based on method calls from the client.
 */
public class GUIController implements ObserverGUI {

    private final GUI gui;
    private final GUIApplication guiApplication;
    private String nickname;
    private HashMap<String, Integer> nickMap;

    private final boolean useBridges = false;

    private UserActionType nextUserAction;

    private boolean characterAbilityState = false;
    private List<RequestParameter> requestParameters;
    private final List<Integer> characterParameters;

    public GUIController(GUI gui) {
        this.gui = gui;
        guiApplication = GUIApplication.getInstance();
        guiApplication.setObserver(this);
        this.characterParameters = new ArrayList<>();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void displayError(String errorDescription, boolean isGameEnding) {
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            Stage stage = (Stage) errorDialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText("Wrong action!");
            errorDialog.setContentText(errorDescription + ". Please choose another move!");
            errorDialog.showAndWait();
            if(isGameEnding)
                endGame();
        });
    }

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

    public void switchToLogin(){
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToLogin);
    }

    @Override
    public void notifyIP() {
        gui.notifySetupInput();
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

    @Override
    public void notifyNickname() {
        gui.notifySetupInput();
    }

    public String getNicknameChosen(){
        return ( (TextField) guiApplication.lookup("nickField")).getText();
    }

    public void connectWithNicknameSuccessful() {
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToGameSetup);
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

    @Override
    public void notifyGameSettings() {
        gui.notifySetupInput();
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
        });
    }

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

    public void towerColorSuccessful(){
        GUIApplication.runLaterExecutor.execute(() -> guiApplication.lookup("colorChoice").setDisable(true));
    }

    @Override
    public void notifyTowerColor() {
        gui.notifySetupInput();
    }

    public TowerColor getTowerColorChosen(){
        String choice = ( (ChoiceBox<String>) guiApplication.lookup("colorChoice") ).getValue();
        return TowerColor.valueOf(choice == null ? null : choice.toUpperCase());
    }

    @Override
    public void notifyWizardType() {
        gui.notifySetupInput();
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void startGame() {
        GUIApplication.runLaterExecutor.execute(guiApplication::switchToMain);
    }

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

    public void updateTurnOrder(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> ((TurnOrderPane)guiApplication.lookup("turnOrderPane"))
                .updateTurnOrderPane(game.getCurrentPhase(), game.getCurrentPlayer(), game.getPlayerOrder()));
    }

    public void updateAssistants(String player, Integer assistantUsed, List<Integer> assistantsLeft){
        GUIApplication.runLaterExecutor.execute(() -> ((PlayerPane)guiApplication.lookup("playerPane" + nickMap.get(player)))
                .updateAssistants(assistantUsed, assistantsLeft));
    }

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

    public void updateCloud(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            for(Integer cloudID : game.getCloudIDs()){
                archipelagoPane.updateCloud(cloudID, game.getCloudStudentsIDs(cloudID));
            }
            archipelagoPane.updateBag(game.getBagStudentsLeft());
        });
    }

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

    public void updateCharacters(ObservableByClient game){
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            for(Integer charID : game.getDrawnCharacterIDs())
                archipelagoPane.updateCharacter(charID, game.getActiveCharacterID() == charID, game.getActiveCharacterUsesLeft(),
                        game.getCharacterStudents(charID), game.getNoEntryTilesCharacter(charID), game.getCharacterOvercharge(charID));
            archipelagoPane.updateCoinHeap(game.getCoinsLeft());
        });
    }

    public void updateCharacterRequest(ObservableByClient game){
        this.requestParameters = game.getCurrentRequestParameters();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void enableAssistants() {
        nextUserAction = UserActionType.PLAY_ASSISTANT;
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectAssistant();
        });
    }

    public void disableAssistants() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectAssistant();
        });
    }

    public void enableEntrance(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsEntrance();
        });
    }

    public void enableEntrance(UserActionType nextUserAction){
        this.nextUserAction = nextUserAction;
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsEntrance();
        });
    }

    public void enableDRStudents(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectStudentsDR();
        });
    }

    public void disableDRStudents(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectStudentsDR();
        });
    }

    public void disableEntrance(){
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectStudentsEntrance();
        });
    }

    public void enableIslands() {
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.enableSelectIsland();
        });
    }

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

    public void disableIslands() {
        GUIApplication.runLaterExecutor.execute(() -> {
            ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
            archipelagoPane.disableSelectIsland();
        });
    }

    public void enableClouds() {
        nextUserAction = UserActionType.TAKE_FROM_CLOUD;
        GUIApplication.runLaterExecutor.execute(() -> {
            CloudContainerPane cloudContainerPane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
            cloudContainerPane.enableSelectCloud();
        });
    }

    public void disableClouds() {
        GUIApplication.runLaterExecutor.execute(() -> {
            CloudContainerPane cloudContainerPane = (CloudContainerPane) guiApplication.lookup("cloudContainerPane");
            cloudContainerPane.disableSelectCloud();
        });
    }

    public void enableTables() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectTables();
        });
    }

    public void enableTables(Color color) {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.enableSelectTables(color);
        });
    }

    public void disableTables() {
        GUIApplication.runLaterExecutor.execute(() -> {
            PlayerPane playerPane = ((PlayerPane) guiApplication.lookup("playerPane" + nickMap.get(nickname)));
            playerPane.disableSelectTables();
        });
    }

    public void enableCharacters(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectCharacter();
        });
    }

    public void disableCharacters(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectCharacter();
        });
    }

    public void enableCharacterAbility(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableActivateCharacter();
        });
    }

    public void disableCharacterAbility(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableActivateCharacter();
        });
    }

    public void enableStudentChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectStudents();
        });
    }

    public void disableStudentChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectStudents();
        });
    }

    public void enableColorChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.enableSelectColor();
        });
    }

    public void disableColorChar(){
        GUIApplication.runLaterExecutor.execute(() -> {
            CharContainerPane charContainerPane = (CharContainerPane) guiApplication.lookup("charContainerPane");
            charContainerPane.disableSelectColor();
        });
    }

    public void enableEndTurn(){
        nextUserAction = UserActionType.END_TURN;
        GUIApplication.runLaterExecutor.execute(() -> {
            Button endTurn = (Button) guiApplication.lookup("endButton");
            endTurn.setVisible(true);
        });
    }

    public void disableEndTurn(){
        GUIApplication.runLaterExecutor.execute(() -> {
            Button endTurn = (Button) guiApplication.lookup("endButton");
            endTurn.setVisible(false);
        });
    }
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Notifies gui that a card has been chosen. Used by assistant cards.
     */
    @Override
    public void notifyAssistantCard() {
        if (nextUserAction == UserActionType.PLAY_ASSISTANT)
            gui.sendSelection(new PlayAssistantUserAction(nickname, getAssistantCardChosen()));
        else
            System.out.println("Error in notifyAssistantCard: wrong user action");
    }

    /**
     * Finds the player's assistant card pane and retrieves the ID of the selected card. Used by gui.
     *
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

    public int getStudentChar() {
        CharContainerPane chars = (CharContainerPane) guiApplication.lookup("charContainerPane");
        return chars.getStudentChosen();
    }

    @Override
    public void notifyCloud() {
        nextUserAction = null;
        gui.sendSelection(new TakeFromCloudUserAction(nickname, getCloudChosen()));
    }

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

    public int getIslandChosen() {
        ArchipelagoPane archipelagoPane = (ArchipelagoPane) guiApplication.lookup("archipelagoPane");
        return archipelagoPane.getIslandChosen();
    }

    @Override
    public void notifyCharacter() {
        gui.sendSelection(new UseCharacterUserAction(nickname, getCharacterChosen()));
    }

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

    public void disableAll(){
        guiApplication.disableAll();
    }

    public void enableAll(){
        guiApplication.enableAll();
    }

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

    public void endGame(){
        GUIApplication.runLaterExecutor.execute(() -> {
            Alert endGameDialogue = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage) endGameDialogue.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/general/icon.png"));
            endGameDialogue.setTitle("End");
            endGameDialogue.setHeaderText("The end");
            endGameDialogue.setContentText("end.");
            endGameDialogue.showAndWait();
            guiApplication.createLoginScene();
            guiApplication.createGameSetupScene();
            guiApplication.createMainScene();
            gui.reset();
        });
    }

}
