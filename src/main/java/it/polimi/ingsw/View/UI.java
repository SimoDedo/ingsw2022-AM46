package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * Interface that defines all methods that a UI must implement in order to interact with the Client.
 */
public interface UI {

    /**
     * Sets the nickname associated with this UI
     * @param nickname the nickname chosen
     */
    void setNickname(String nickname);

    /**
     * Starts the game
     */
    void startGame();

    /**
     * Updates available commands. Each command represents an action that the user can take.
     * @param toDisable the commands that are to be disabled
     * @param toEnable the commands that are to be enabled
     */
    void updateCommands(List<Command> toDisable, List<Command> toEnable);

    /**
     * Called to notify that the server sent a response to a previous user action.
     */
    void notifyServerResponse();

    /**
     * Requests the user to input the server information to connect.
     * @param defaultIP the default IP to connect to
     * @param defaultPort the default port to connect to
     * @return A Map containing both the IP and the Port in a string format. Their keys are respectively "IP" and "port"
     */
    Map<String, String> requestServerInfo(String defaultIP, int defaultPort);

    /**
     * Requests the user to choose a nickname
     * @return the nickname chosen
     */
    String requestNickname();

    /**
     * Requests the user to choose the game settings.
     * Then, sends it to the server.
     */
    void requestGameSettings();

    /**
     * Requests the user to choose a tower color.
     * Then, sends it to the server.
     * @param game the game used to retrieve the new tower colors left from.
     */
    void requestTowerColor(ObservableByClient game);

    /**
     * Requests the user to choose a wizard type.
     * Then, sends it to the server.
     * @param game the game used to retrieve the new wizard types left from.
     */
    void requestWizard(ObservableByClient game);

    /**
     * Requests the user to wait until all players are ready to play.
     */
    void requestWaitStart();

    /**
     * Displays given information to the user.
     * @param info the information to be displayed
     */
    void displayInfo(String info);

    /**
     * Displays an error that occurred to the user.
     * @param error the error to display.
     * @param isFatal true if said error causes the game to stop
     */
    void displayError(String error, boolean isFatal);

    /**
     * Updates available parameters to select during setup.
     * @param game the game used to retrieve the new parameters from.
     * @param actionTaken the userAction that caused this function to be called.
     */
    void updateSetup(ObservableByClient game, UserActionType actionTaken);

    /**
     * Displays the current board to the user
     * @param game the game used to retrieve the new data from.
     * @param actionTaken the userAction that caused this function to be called.
     */
    void displayBoard(ObservableByClient game, UserActionType actionTaken);

    /**
     * Displays the game winners. Then, ends the game
     * @param winner the tower color of the winner of thi match.
     * @param winners the nicknames of the winners of this match.
     * @param losers the nicknames of the losers of this match.
     */
    void displayWinners(TowerColor winner, List<String> winners, List<String> losers);

}
