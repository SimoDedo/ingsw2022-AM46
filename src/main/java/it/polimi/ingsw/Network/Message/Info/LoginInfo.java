package it.polimi.ingsw.Network.Message.Info;

import java.util.List;

public class LoginInfo extends Info{

    private List<String> loggedPlayers;

    public LoginInfo(List<String> loggedPlayers) {
        super("Login successful!");
        this.loggedPlayers = loggedPlayers;
    }

    public List<String> getLoggedPlayers() {
        return loggedPlayers;
    }
}
