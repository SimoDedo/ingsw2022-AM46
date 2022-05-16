package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.UserAction.PingUserAction;

@SuppressWarnings("BusyWait")
public class Ping implements Runnable{

    private int sentNotReceived;

    private boolean isActive;

    private Client client;

    public Ping(Client client){
        this.client = client;
        sentNotReceived = 0;
    }

    @Override
    public void run() {
        isActive = true;
        while (isActive){
            client.sendUserAction(new PingUserAction(""));
            sent();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                isActive = false;
            }
        }
    }

    private void sent(){
        sentNotReceived++;
    }

    public void  received(){
        sentNotReceived--;
    }
}
