package it.polimi.ingsw.Network.Message.Error;

/**
 * Error sent by the server to all connected clients whenever a client has disconnected.
 */
public class DisconnectionError extends Error{
    public DisconnectionError(String errorDescription) {
        super("Server", errorDescription);
    }
}
