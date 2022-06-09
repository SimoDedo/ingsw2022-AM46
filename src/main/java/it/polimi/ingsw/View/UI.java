package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public interface UI {

    void setNickname(String nickname);

    void startGame();

    void updateCommands(List<Command> toDisable, List<Command> toEnable);

    void notifyServerResponse();

    Map<String, String> requestServerInfo(String defaultIP, int defaultPort);

    String requestNickname();

    void requestGameSettings();

    void requestTowerColor(ObservableByClient game);

    void requestWizard(ObservableByClient game);

    void requestWaitStart();

    void displayInfo(String info);

    void displayError(String error, boolean isFatal);

    void updateSetup(ObservableByClient game, UserActionType actionTaken);

    void displayBoard(ObservableByClient game, UserActionType actionTaken);

    void displayWinners(TowerColor winner, List<String> winners);

}
