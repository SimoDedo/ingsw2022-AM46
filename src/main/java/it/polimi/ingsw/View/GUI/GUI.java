package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.GameModel.ObservableByClient;
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

    private GUIController guiController;

    public GUI(Client client) {
        guiController = new GUIController(this);
    }

    @Override
    public void setNickname(String nickname) {

    }

    @Override
    public void update(ObservableByClient game) {

    }

    @Override
    public void startGame() {

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
}
