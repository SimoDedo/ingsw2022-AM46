package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.View.Client;
import it.polimi.ingsw.View.UI;

import java.util.List;
import java.util.Map;

/**
 * This class manages the GUIApplication, which stores and displays the graphical interface, via the
 * GUIController class. It serves as a driver between the client and the GUI proper.
 */
public class GUI implements UI {

    /**
     * The client associated with this GUI.
     */
    private final Client client;

    /**
     * The nickname of the player using this GUI.
     */
    private String nickname;

    /**
     * The controller of the GUI.
     */
    private final GUIController guiController;

    /**
     * Lock used to synchronize and wait an input.
     */
    private final Object waitInputLock;
    /**
     * True if the GUI is currently waiting on an input.
     */
    private boolean waitingInput;

    /**
     * True if the GUI in being reset.
     */
    private boolean resetting;
    /**
     * True if the user has logged in with a username.
     */
    private boolean loggedIn;
    /**
     * True if the user has chosen a tower color.
     */
    private boolean chosenTC;

    /**
     * Constructor of the GUI.
     * @param client the client associated with this GUI.
     */
    public GUI(Client client) {
        this.client = client;
        guiController = new GUIController(this);
        nickname = null;
        waitInputLock = new Object();
        waitingInput = false;

        loggedIn = false;
        chosenTC = false;
        resetting = false;
    }

    /**
     * Closes the Client.
     */
    public void close() {
        client.close();
    }

    /**
     * Resets the GUI, then resets the client.
     */
    public void reset(){
        resetting = true;
        nickname = null;
        waitingInput = false;

        loggedIn = false;
        chosenTC = false;

        notifySetupInput();

        client.reset();
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
        guiController.setNickname(nickname);
    }

    @Override
    public void startGame() {
        guiController.startGame();
    }

    @Override
    public void updateCommands(List<Command> toDisable, List<Command> toEnable) {
        for(Command command : toDisable)
            parseDisableCommand(command);
        for (Command command : toEnable)
            parseEnableCommand(command);
        guiController.reEnableAll();
    }

    /**
     * It parses a given command enabling various GUI elements to be selected according to the command to enable.
     * @param command the command to enable.
     */
    private void parseEnableCommand(Command command){
        switch (command){
            case ASSISTANT -> guiController.enableAssistants();
            case MOVE -> {
                guiController.disableTables();
                guiController.disableIslands();
                guiController.enableEntrance(UserActionType.MOVE_STUDENT);
            }
            case MOTHER_NATURE -> guiController.enableIslands(UserActionType.MOVE_MOTHER_NATURE);
            case CLOUD -> guiController.enableClouds();
            case CHARACTER -> guiController.enableCharacters();
            case ABILITY -> {
                guiController.enableCharacterAbility();
                guiController.enableLast();
            }
            case END_TURN -> guiController.enableEndTurn();
        }
    }

    /**
     * It parses a given command disabling all GUI elements associated with this command.
     * @param command the command to disable.
     */
    private void parseDisableCommand(Command command){
        switch (command){
            case ASSISTANT -> guiController.disableAssistants();
            case MOVE -> {
                guiController.disableEntrance();
                guiController.disableIslands();
                guiController.disableTables();
            }
            case MOTHER_NATURE -> guiController.disableIslands();
            case CLOUD -> guiController.disableClouds();
            case CHARACTER -> guiController.disableCharacters();
            case ABILITY -> {
                guiController.disableCharacterAbility();
                guiController.enableLast();
            }
            case END_TURN -> guiController.disableEndTurn();
        }
    }

    @Override
    public void notifyServerResponse() {

    }

    @Override
    public Map<String, String> requestServerInfo(String defaultIP, int defaultPort) {
        resetting = false;
        //show the scene, but on login already showing.
        guiController.switchToLogin();
        waitSetupInput();
        return guiController.getIPChosen();
    }

    @Override
    public String requestNickname() {
        guiController.connectToIPSuccessful();
        waitSetupInput();
        return guiController.getNicknameChosen();
    }

    @Override
    public void requestGameSettings() {
        guiController.connectWithNicknameSuccessful();
        loggedIn = true;
        guiController.enableGameSettings();
        waitSetupInput();
        if(! resetting)
            client.sendUserAction(
                new GameSettingsUserAction(nickname, guiController.getNumOfPlayerChosen(), guiController.getGameModeChosen() ));
    }

    @Override
    public void requestTowerColor(ObservableByClient game) {
        if(! loggedIn){
            guiController.connectWithNicknameSuccessful();
            loggedIn = true;
        }
        guiController.showGameMode(game);
        guiController.enableTowerWizard();
        guiController.updateTowerWizard(game.getAvailableTowerColors(), game.getAvailableWizards());
        waitSetupInput();
        if(! resetting)
            client.sendUserAction(new TowerColorUserAction(this.nickname, guiController.getTowerColorChosen()));
    }

    @Override
    public void requestWizard(ObservableByClient game) {
        guiController.enableTowerWizard();
        if(!chosenTC){
            chosenTC = true;
            guiController.towerColorSuccessful();
        }
        else
            waitSetupInput();
        //guiController.updateTowerWizard(game.getAvailableTowerColors(), game.getAvailableWizards());
        // causes automatic selection to be reset, however it's needed if somehow the real time updating fails. but it shouldn't so it's removed for now
        if(! resetting)
            client.sendUserAction(new WizardUserAction(this.nickname, guiController.getWizardChosen()));
    }

    @Override
    public void updateSetup(ObservableByClient game, UserActionType actionTaken) {
        guiController.updateTowerWizard(game.getAvailableTowerColors(), game.getAvailableWizards());
    }

    @Override
    public void requestWaitStart() {
        guiController.waitForStart();
    }

    @Override
    public void displayInfo(String info) {
        guiController.displayInfo(info);
    }

    @Override
    public void displayError(String error, boolean isFatal) {
        guiController.displayError(error, isFatal);
    }

    @Override
    public void displayBoard(ObservableByClient game, UserActionType actionTaken) {
        if(actionTaken != null){
            switch (actionTaken){
                case WAIT_GAME_START -> guiController.initialDraw(game, nickname);
                case PLAY_ASSISTANT -> {
                    guiController.updateCloud(game);
                    guiController.updateTurnOrder(game);
                    for(String nick : game.getPlayers())
                        guiController.updateAssistants(nick, game.getCardsPlayedThisRound().get(nick), game.getCardsLeft(nick));
                }
                case MOVE_STUDENT ->{
                    guiController.updateCharacters(game);
                    guiController.updateTurnOrder(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateArchipelago(game);
                }
                case MOVE_MOTHER_NATURE -> {
                    guiController.updateCharacters(game);
                    guiController.updateArchipelago(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateCloud(game);
                }
                case TAKE_FROM_CLOUD -> {
                    guiController.updateCharacters(game);
                    guiController.updateCloud(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateTurnOrder(game);
                }
                case USE_CHARACTER -> {
                    guiController.updateArchipelago(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateCharacters(game);
                    guiController.updateCharacterRequest(game);
                }
                case USE_ABILITY -> {
                    guiController.updatePlayerBoards(game);
                    guiController.updateArchipelago(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateCharacters(game);
                    guiController.updateCharacterRequest(game);
                }
                case END_TURN -> {
                    guiController.updateCharacters(game);
                    guiController.updateTurnOrder(game);
                    guiController.updateCloud(game);
                    guiController.updatePlayerBoards(game);
                    for(String nick : game.getPlayers())
                        guiController.updateAssistants(nick, game.getCardsPlayedThisRound().get(nick), game.getCardsLeft(nick));
                }
            }
        }
        else {
            guiController.updateCharacters(game);
            guiController.updateTurnOrder(game);
            guiController.updatePlayerBoards(game);
            guiController.updateArchipelago(game);
            guiController.updateCloud(game);
        }
    }

    @Override
    public void displayWinners(TowerColor winner, List<String> winners, List<String> losers) {
        guiController.displayWinners(winner, winners, losers);
    }

    /**
     * Sends the user action given through the client. Then, disables all GUI elements to avoid errors.
     * The elements will have to be enabled again when a server response has been received.
     * @param userAction the user action to send.
     */
    public void sendSelection(UserAction userAction){
        guiController.disableAllTemporary();
        client.sendUserAction(userAction);
    }

    /**
     * Puts the GUI in a waiting state until awoken.
     */
    private void waitSetupInput(){
        synchronized (waitInputLock){
            waitingInput = true;
            while (waitingInput){
                try {
                    waitInputLock.wait();
                } catch (InterruptedException e) {
                    //No one interrupts this thread
                    System.exit(-1);
                }
            }
        }
    }

    /**
     * Awakes the GUI if it was waiting for a setup input.
     */
    public void notifySetupInput(){
        synchronized (waitInputLock){
            waitingInput = false;
            waitInputLock.notifyAll();
        }
    }
}
