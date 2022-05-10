package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;

import java.util.Map;

public interface UI {

    void askTryConnecting();

    Map<String, String> askServerInfo();

    void setNickname(String nickname);

    void setGame(ObservableByClient game);

    String requestNickname();

    void requestGameSettings();

    void requestTowerColor();

    void requestWizard();

    void showText(String text);

    void showInfo(String info);

    void showError(String error);

    void reset();
}
