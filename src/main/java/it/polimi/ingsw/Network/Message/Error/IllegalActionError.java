package it.polimi.ingsw.Network.Message.Error;

/**
 * Error that is sent by the server whenever a user has made a move that it is not allowed to.
 * If this error is sent, it means that the user has tried to do something it shouldn't be allowed to do.
 */
public class IllegalActionError extends Error{
    public IllegalActionError(String errorDescription) {
        super("Server", errorDescription);
    }
}
