package it.polimi.ingsw.Network.Message.Error;

/**
 * Error sent whenever a login error occurs.
 * Most of the time it is sent when a user has chosen an illegal nickname
 */
public class LoginError extends Error{
    public LoginError(String errorDescription) {
        super("Server", errorDescription);
    }
}
