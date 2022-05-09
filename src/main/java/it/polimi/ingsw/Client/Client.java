package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.cli.CLI;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Network.Message.UserAction.MoveStudentUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Phase;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {

    private final String nickname;
    private Game game;
    private final UI ui;

    public Client(String nickname, Game game, UI ui) {

        this.nickname = nickname;
        this.game = game;
        this.ui = ui;
    }

    public Phase getPhase(){
        return Phase.IDLE;

    }

    public String getNickname(){
        return nickname;
    }


    /**
     * used to move students from source to any location
     * @param studentColor of the student to move
     * @return true if move is allowed
     */
    public boolean requestMove(Color studentColor, int destinationID){
        int studentID = -1;
        HashMap<Integer, Color> studentIDs = game.getEntranceStudentsIDs(getNickname());
        for(Map.Entry<Integer, Color> entry : studentIDs.entrySet()){
            if (entry.getValue() == studentColor) { studentID = entry.getKey(); break; }
        }
        if(studentID == -1){ ui.displayMessage("No student of selected color in entrance"); return false; }
        UserAction request = new MoveStudentUserAction(getNickname(), studentID, destinationID);
        // send request and verify
        return true;

    }

    public boolean requestCloud(int readBoundNumber) {
        return true;
    }

    public boolean requestCharacter(int characterID) {
        return true;
    }

    public boolean requestAssistant(int assistantID) {
        return true;
    }

    public boolean requestTowerColor(String readLineFromSelection) {
        return true;
    }

    public boolean requestGameMode(int readBoundNumber) {
        return true;
    }

    public boolean requestWizard(String readLineFromSelection) {
        return true;
    }

    public boolean requestLogin(String nickname) {
        return true;
    }
}

