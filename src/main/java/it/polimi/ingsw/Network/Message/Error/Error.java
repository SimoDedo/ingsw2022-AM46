package it.polimi.ingsw.Network.Message.Error;

import it.polimi.ingsw.Network.Message.Message;

/**
 * Generic error used as a base for the errors that are actually sent.
 */
public abstract class Error extends Message {

    private final String errorDescription;

    public Error(String sender, String errorDescription) {
        super(sender);
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return ( errorDescription);
    }
}
