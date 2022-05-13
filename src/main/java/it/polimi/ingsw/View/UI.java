package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Command;
import it.polimi.ingsw.Utils.Enum.Phase;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

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

    public void displayLogin();

    public void displayBoard();


    public void displayMessage(String message);

    void displayMessage(String message, String color);

    public void requestLogin();

    public void requestTowerColor();

    public void requestGameMode();

    public void requestPlayerNumber();

    public void requestWizard ();

    public void requestAssistant();

    void requestMoveFromEntrance();

    public void requestMotherNature();

    public void requestCloud();

    public void requestCharacter();

    public void displayHelp();

    public void displayHelp(String context);

    public void displayUnavailable();

    public void displayInvalid();

    public void standings();

    public void displayEntrance(String nickname);

    public void displayArchipelago();

    public void displayTables(String nickname);

    public void displayClouds();

    public void displayCharacters();

    public void displayHand(String nickname);

    public void enableCommand(Command command);

    public void disableCommand(Command command);

    void notifyServerResponse();

    void displayAvailableCommands();

    void parseCommand();
}
