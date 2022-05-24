package it.polimi.ingsw.Network.Message;

import java.io.Serializable;

/**
 * Serializable class that models a generic message sent between server and client.
 */
public abstract class Message implements Serializable {

    /**
     * Whoever sent this message.
     */
    private final String sender;

    public Message(String sender){
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

}
