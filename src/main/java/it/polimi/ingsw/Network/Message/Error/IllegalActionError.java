package it.polimi.ingsw.Network.Message.Error;

public class IllegalActionError extends Error{
    public IllegalActionError(String errorDescription) {
        super("Server", errorDescription);
    }
}
