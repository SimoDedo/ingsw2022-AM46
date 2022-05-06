package it.polimi.ingsw.Client.cli;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.UI;
import it.polimi.ingsw.Utils.Enum.Phase;

import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.util.Scanner;

public class CLI implements UI {

    private final PrintStream output;
    private final Client client;

    public CLI(Client client){
        output = new PrintStream(System.out);
        this.client = client;
    }

    @Override
    public void displayLogin() {

    }

    @Override
    public void displayBoard() {

    }

    @Override
    public void displayMessage(String message) {
        message = message + "\n";
        System.out.println(message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public void displayHelp(){
        Phase phase = client.getPhase();
        displayMessage("List of available commands:\nhelp - display this menu\nstandings - display current game standings");
        switch (phase) {
            case IDLE -> displayMessage("");
            case PLANNING -> displayMessage("""
                    cloud - Type the ID of the cloud you would like to choose.
                    move - Type the ID of the student you would like to move, followed by the word 'table' if you would like to move the student to its corresponding table, or the ID of the island otherwise.
                    """);
            case ACTION -> displayMessage("""
                    play assistant - Type the ID of the assistant you would like to play.
                    play character - Type the ID of the character you would like to purchase.
                    end turn - request end turn.
                    """
            );
        }
    }


    public void displayHelp(String context){
        switch (context){
            case "": case "\n": break;
            case "choose cloud": displayMessage("Type the ID of the cloud you would like to choose."); break;
            case "move": displayMessage("Type the ID of the student you would like to move, followed by the word " +
                    "'table' if you would like to move the student to its corresponding table, or the ID of the " +
                    "island otherwise."); break;
            case "play assistant": displayMessage("Type the ID of the assistant you would like to play."); break;
            case "play character": displayMessage("Type the ID of the character you would like to purchase."); break;
            default: displayMessage("Invalid command - please type help for a list of available commands."); break;
        }

    }


    public void requestCloud(){
        displayMessage("Type the ID of the cloud you would like to choose.");
    }


    public void requestMove(){}


    public void requestCharacter(){}


    public void requestAssistant(){}


    public void standings(){}


    public void requestEndTurn(){}

}
