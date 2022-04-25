package it.polimi.ingsw.Network.Message.Error;

public class LoginError extends Error{
    public LoginError(String errorDescription) {
        super("Server", errorDescription);
    }
}
