package it.polimi.ingsw.Network.Client;

import it.polimi.ingsw.Network.Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Listens for server answers and forwards them to ActionHandler ... ?
 */
public class ListenerSocket implements Runnable{

    private final Socket socket;
    private final ActionHandler actionHandler;
    ObjectInputStream inputStream;

    public ListenerSocket(Socket socket, ActionHandler actionHandler, ObjectInputStream inputStream){
        this.socket = socket;
        this.actionHandler = actionHandler;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            Message message = (Message) inputStream.readObject();
            // message to be forwarded to actionHandler
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());

            }
        }
    }
}
