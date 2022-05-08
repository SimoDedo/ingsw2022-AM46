package it.polimi.ingsw.View;

import java.util.Map;

public interface UI {

    void askTryConnecting();

    Map<String, String> askServerInfo();

    void setNickname(String nickname);

    String askNickname();

    void askGameSettings();

    void askTowerColor(int numOfPlayers);

    void askWizard();

    void showText(String text);

    void showInfo(String info);

    void showError(String error);

    void reset();
}
