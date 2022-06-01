package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.Command;
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
    }

    public void close() {
        client.reset();
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void startGame() {
        guiController.startGame();
    }

    @Override
    public void updateCommands(List<Command> toDisable, List<Command> toEnable) {

    }

    @Override
    public void notifyServerResponse() {

    }

    @Override
    public Map<String, String> requestServerInfo(String defaultIP, int defaultPort) {
        //show the scene, but on login already showing.
        waitInput();
        return guiController.getIPChosen();
    }

    @Override
    public String requestNickname() {
        guiController.connectToIPSuccessful();
        waitInput();
        return guiController.getNicknameChosen();
    }

    @Override
    public void requestGameSettings() {
        guiController.connectWithNicknameSuccessful();
        loggedIn = true;
        guiController.enableGameSettings();
        waitInput();
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
        waitInput();
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
            waitInput();
        //guiController.updateTowerWizard(game.getAvailableTowerColors(), game.getAvailableWizards());
        // causes automatic selection to be reset, however it's needed if somehow the real time updating fails. but it shouldn't so it's removed for now
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
    public void displayError(String error, boolean isUrgent) {
        guiController.displayError(error);
    }

    @Override
    public void displayBoard(ObservableByClient game, UserActionType actionTaken) {
        switch (actionTaken){
            case WAIT_GAME_START -> guiController.initialDraw(game);
        }

    }

    private void waitInput(){
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

    public void notifyInput(){
        synchronized (waitInputLock){
            waitingInput = false;
            waitInputLock.notifyAll();
        }
    }
}
