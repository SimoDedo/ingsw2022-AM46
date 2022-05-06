package it.polimi.ingsw.Client.cli;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.UI;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Phase;

import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CLI implements UI {

    private final PrintStream output;
    private final Client client;
    private final InputParser parser;
    private final Game game;

    public CLI(Client client, InputParser parser, Game game){
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = parser;
        this.game = game;
    }

    @Override
    public void displayLogin() {

    }

    @Override
    public void displayBoard() {
        standings();
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
                    move - Type the ID of the student you would like to move, followed by 1 or 2 to specify the type of destination (table or island). In case the destination is an island, you must also specify the ID.
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
            case "move": displayMessage("Type the ID of the student, followed by either 1 or 2 to specify the type of destination (table or island)." +
                    "In case the destination is an island, you must also specify the ID."); break;
            case "play assistant": displayMessage("Type the ID of the assistant you would like to play."); break;
            case "play character": displayMessage("Type the ID of the character you would like to purchase."); break;
            default: displayMessage("Invalid command - please type help for a list of available commands."); break;
        }

    }


    public void requestCloud(){
        displayMessage("Type the ID of the cloud you would like to choose.");
    }


    public void requestMove(){
        int studentID;
        int destinationID;
        if(client.getPhase() == Phase.PLANNING) {
            displayEntrance(client.getNickname());
            displayMessage("Type the ID of the student you would like to move:");

            boolean valid = true; // obv temporary + add message if the int is out of range + add option to exit?
            do {
                studentID = parser.readNumber();
            } while(!valid);


        } else displayUnavailable();

    }

    public void parseCommand(){
        String command = parser.readLine();
        switch (command){
            case "": case "\n": break;
            case "help": displayHelp(); break;
            case "choose cloud": requestCloud(); break;
            case "move": requestMove(); break;
            case "play assistant": requestAssistant(); break;
            case "play character": requestCharacter(); break;
            case "standings": standings(); break;
            case "end turn": requestEndTurn(); break;
            default: displayInvalid(); break;
        }
    }


    public void requestCharacter(){}


    public void displayUnavailable(){
        displayMessage("You cannot execute this command at this time.");
    }

    public void displayInvalid(){
        displayMessage("Unrecognized command - please type help for a list of available commands.");
    }

    public void requestAssistant(){

    }


    public void standings(){}


    public void displayEntrance(String nickname){

        HashMap<Integer, Color> entranceIDs = game.getEntranceStudentsIDs(nickname);
        if(nickname.equals(client.getNickname())){ nickname = "your"; } else nickname += "'s";

        StringBuilder entranceInfo = new StringBuilder(String.format("These are the students in %s entrance", nickname));

        for(int ID : entranceIDs.keySet()){
            displayMessage(String.format("Student %s with ID %d", entranceIDs.get(ID), ID));
        }

    }

    public void displayIslands(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();


        StringBuilder islandGroupInfo = new StringBuilder("These are the Island Groups and the IDs of the Island Tiles they contain:\n");

        for(int islandGroupIdx : islandGroups.keySet()){

            islandGroupInfo.append(String.format("Island group %d contains the following Island Tile IDs: ", islandGroupIdx));
            for(int islandTileID : islandGroups.get(islandGroupIdx)){
                islandGroupInfo.append(String.format(" %d,", islandTileID));
            }
        }
        displayMessage(islandGroupInfo.toString());
    }


    public void displayTables(String nickname){

        StringBuilder tableInfo = new StringBuilder("These are the tables ");

        for(Color c : Color.values()) {

            game.getTableStudentsIDs(nickname, c);
        }


    }


    public void requestEndTurn(){}

}
