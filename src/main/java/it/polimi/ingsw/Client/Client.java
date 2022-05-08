package it.polimi.ingsw.Client;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Phase;

public class Client {

    private final String nickname;

    public Client(String nickname){
        this.nickname = nickname;
    }

    public Phase getPhase(){
        return Phase.IDLE;

    }

    public String getNickname(){
        return nickname;
    }

    /**
     * used to move students from entrance to islands
     * @param color of the student to move
     * @param islandGroupIdx index of the island group to move it to
     * @return true if move is allowed
     */
    public boolean requestMove(Color color, int islandGroupIdx) {
        return true;
    }

    /**
     * used to move students from entrance to dining room
     * @param color of the student to move
     * @return true if move is allowed
     */
    public boolean requestMove(Color color){
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
}
