package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.UserAction.PingUserAction;

/**
 * Class that implements runnable.
 * When ran, it starts a ping with the server.
 */
@SuppressWarnings("BusyWait")
public class Ping implements Runnable{

    private int sentNotReceived;

    /**
     * Boolean that represents whether this ping is running
     */
    private boolean isActive;

    /**
     * The client used to send the ping.
     */
    private final Client client;

    public Ping(Client client){
        this.client = client;
        sentNotReceived = 0;
    }

    /**
     * Starts the ping loop. Every tot milliseconds a ping user action gets sent.
     */
    @Override
    public void run() {
        isActive = true;
        while (isActive){
            client.sendUserAction(new PingUserAction(""));
            sent();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                isActive = false;
            }
        }
    }

    /**
     * Used to register a ping sent.
     */
    private void sent(){
        sentNotReceived++;
    }

    /**
     * Used to register a ping received.
     */
    public void  received(){
        sentNotReceived--;
    }
}
