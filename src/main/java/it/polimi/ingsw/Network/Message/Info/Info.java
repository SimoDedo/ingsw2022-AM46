package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Network.Message.Message;

/**
 * Class that models a request sent by the server to a client.
 * It contains a string describing the request, and a list of RequestParameters.
 * the client who receives this list will proceed with a selection to create a UserAction in response to the request.
 */
public class Info extends Message {


    /**
     * Used in toString method
     */
    private final String info;

    public Info(String info) {
        super("Server");
        this.info = info;
    }

    @Override
    public String toString() {
        return info;
    }
}
