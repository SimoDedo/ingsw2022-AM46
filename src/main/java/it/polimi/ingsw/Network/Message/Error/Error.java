package it.polimi.ingsw.Network.Message.Error;

import it.polimi.ingsw.Network.Message.Message;

public abstract class Error extends Message {

    private String errorDescription;

    public Error(String sender, String errorDescription) {
        super(sender);
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return ( errorDescription);
    }
}
