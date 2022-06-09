package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Network.Message.Message;

/**
 * Class that models generic information sent by the server to a client.
 * It contains a string describing the information.
 */
public abstract class Info extends Message {

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
