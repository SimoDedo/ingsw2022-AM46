package it.polimi.ingsw;

import it.polimi.ingsw.View.ClientDraft;

public class ClientApp {
    public static void main(String[] args){
        for (int i = 0; i < args.length; i++) {

        }
        ClientDraft clientDraft = new ClientDraft();

        clientDraft.start();
    }
}
