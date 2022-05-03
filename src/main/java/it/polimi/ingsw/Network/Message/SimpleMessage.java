package it.polimi.ingsw.Network.Message;

public class SimpleMessage extends Message{

    String content;

    public SimpleMessage(String sender, String content) {
        super(sender);
        this.content = content;
    }
}
