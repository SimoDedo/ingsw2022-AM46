package it.polimi.ingsw.Network.Message.Error;

public class IllegalSelectionError extends Error{

    public IllegalSelectionError(String errorDescription) {
        super("Server", errorDescription);
    }
}
