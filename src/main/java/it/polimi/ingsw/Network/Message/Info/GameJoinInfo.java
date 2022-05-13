package it.polimi.ingsw.Network.Message.Info;

import java.util.List;

public class GameJoinInfo extends Info{

    private List<String> loggedPlayers;

    public GameJoinInfo(List<String> loggedPlayers) {
        super("Login successful!");
        this.loggedPlayers = loggedPlayers;
    }

    public List<String> getLoggedPlayers() {
        return loggedPlayers;
    }
}
