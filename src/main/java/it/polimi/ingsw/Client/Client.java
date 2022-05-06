package it.polimi.ingsw.Client;

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
}
