package it.polimi.ingsw.Client.gui;

import it.polimi.ingsw.Client.UI;

import java.beans.PropertyChangeEvent;

public class GUI implements UI {
    public GUI(){}

    public void displayLogin(){}

    public void displayBoard(){}

    public void displayMessage(String message){}

    public void propertyChange(PropertyChangeEvent evt){
        switch (evt.getPropertyName()){
            case "pickStudent":
            case "playAssistant":
            case "playCharacter":
            case "boardUpdate":
            case "select":
            case "win":

        }
    }
}
