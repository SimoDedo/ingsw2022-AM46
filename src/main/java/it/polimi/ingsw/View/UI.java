package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public interface UI {

    void setNickname(String nickname);

    void update(ObservableByClient game);

    void startGame();

    void updateCommands(List<Command> toDisable, List<Command> toEnable);

    void notifyServerResponse(boolean gameStarted);

    Map<String, String> requestServerInfo(String defaultIP, int defaultPort);

    String requestNickname();

    void requestGameSettings();

    void requestTowerColor(ObservableByClient game);

    void requestWizard(ObservableByClient game);

    void displayMessage(String message);

    void displayInfo(String info);

    void displayError(String error, boolean isUrgent);

    void displayBoard(ObservableByClient game, UserActionType actionTaken);

}
