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

    private Client client;

    private String nickname;

    private GUIController guiController;

    private final Object waitInputLock;
    private boolean waitingInput;

    private boolean resetting;
    private boolean loggedIn;
    private boolean chosenTC;

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

    public void close() {
        client.close();
    }

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
        guiController.enableAll();
    }

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
        }
    }

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
        guiController.showTowerWizard();
        guiController.updateTowerWizard(game.getAvailableTowerColors(), game.getAvailableWizards());
        waitSetupInput();
        if(! resetting)
            client.sendUserAction(new TowerColorUserAction(this.nickname, guiController.getTowerColorChosen()));
    }

    @Override
    public void requestWizard(ObservableByClient game) {
        guiController.showTowerWizard();
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
                    guiController.updateArchipelago(game);
                    guiController.updatePlayerBoards(game);
                    guiController.updateCharacters(game);
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
    public void displayWinners(TowerColor winner, List<String> winners) {
        guiController.displayWinners(winner, winners);
    }

    public void sendSelection(UserAction userAction){
        guiController.disableAll();
        client.sendUserAction(userAction);
    }

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

    public void notifySetupInput(){
        synchronized (waitInputLock){
            waitingInput = false;
            waitInputLock.notifyAll();
        }
    }
}
