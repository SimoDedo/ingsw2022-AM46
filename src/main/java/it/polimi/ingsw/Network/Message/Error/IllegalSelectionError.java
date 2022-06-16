package it.polimi.ingsw.Network.Message.Error;

/**
 * Error that is sent by the server whenever a user action is taken but its parameters can't be interpreted or
 * used correctly to make said action server side.
 * If this error is sent, it means that the user was able to select something that it shouldn't have been able to be selected.
 */
public class IllegalSelectionError extends Error{

    public IllegalSelectionError(String errorDescription) {
        super("Server", errorDescription);
    }
}
