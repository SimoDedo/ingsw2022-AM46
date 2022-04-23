package it.polimi.ingsw.Network.Message.Error;

public class SelectionError extends Error{

    public SelectionError(String sender, String errorDescription) {
        super(sender, errorDescription);
    }
}
