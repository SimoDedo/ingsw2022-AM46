package it.polimi.ingsw.Network.Client.cli;

import it.polimi.ingsw.Network.Client.UI;

import java.beans.PropertyChangeEvent;

public class CLI implements UI {
    @Override
    public void displayLogin() {

    }

    @Override
    public void displayBoard() {

    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
