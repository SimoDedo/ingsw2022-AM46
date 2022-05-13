package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.UserAction.PingUserAction;

@SuppressWarnings("BusyWait")
public class Ping implements Runnable{

    private boolean isActive;

    private Client client;

    public Ping(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        isActive = true;
        while (isActive){
            client.sendUserAction(new PingUserAction(""));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                isActive = false;
            }
        }
    }
}
