package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Utils.Enum.Command;

import java.util.*;

public interface UI {

    void update(ObservableByClient game);

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

    void displayLogin();

    void displayBoard();


    void displayMessage(String message);

    void displayMessage(String message, String color);

    void requestLogin();

    void requestTowerColor();

    void requestGameMode();

    void requestPlayerNumber();

    void requestWizard ();

    void requestAssistant();

    void requestMoveFromEntrance();

    void requestMotherNature();

    void requestCloud();

    void requestCharacter();

    void displayHelp();

    void displayHelp(String context);

    void displayUnavailable();

    void displayInvalid();

    void standings();

    void displayEntrance(String nickname);

    void displayArchipelago();

    void displayTables(String nickname);

    void displayClouds();

    void displayCharacters();

    void displayHand(String nickname);

    void enableCommand(Command command);

    void disableCommand(Command command);

    void notifyServerResponse();

    void displayAvailableCommands();

    void parseCommand();
}
