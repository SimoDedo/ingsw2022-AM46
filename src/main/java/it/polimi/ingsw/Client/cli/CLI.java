package it.polimi.ingsw.Client.cli;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.UI;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Phase;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.InputParser;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CLI implements UI {

    private final PrintStream output;
    private final Client client;
    private final InputParser parser;
    private final Game game;
    private final HashMap<Integer, Integer> cloudMap = new HashMap<>();

    public CLI(Client client, InputParser parser, Game game){
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = parser;
        this.game = game;
        for(int cloudID : game.getCloudIDs()){
            cloudMap.put(game.getCloudIDs().indexOf(cloudID), cloudID);
        }
    }


    private String studentFrequencyString(List<Color> students){
        HashMap<Color, Integer> frequencyMap = new HashMap<>();
        for(Color c : students){
            if(frequencyMap.containsKey(c)){
                frequencyMap.put(c, frequencyMap.get(c) + 1);
            } else {
                frequencyMap.put(c, 1);
            }
        }
        StringBuilder colorFrequency = new StringBuilder();
        for(Color c : frequencyMap.keySet()){
            colorFrequency.append(String.format("%d %s students\n", frequencyMap.get(c), c));
        }
        return colorFrequency.toString();
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
            case "": case "\n": 
            case "choose cloud": displayMessage("Type the ID of the cloud you would like to choose."); 
            case "move": displayMessage("Type the ID of the student, followed by either 1 or 2 to specify the type of destination (table or island)." +
                    "In case the destination is an island, you must also specify the ID."); 
            case "play assistant": displayMessage("Type the ID of the assistant you would like to play."); 
            case "play character": displayMessage("Type the ID of the character you would like to purchase."); 
            default: displayMessage("Invalid command - please type help for a list of available commands."); 
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
            case "help"-> displayHelp(); 
            case "choose cloud"-> requestCloud(); 
            case "move"-> requestMove(); 
            case "play assistant"-> requestAssistant(); 
            case "play character"-> requestCharacter(); 
            case "standings"-> standings(); 
            case "end turn"-> requestEndTurn(); 
            default-> displayInvalid(); 
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

        String studentFrequency = studentFrequencyString(new ArrayList<>(game.getEntranceStudentsIDs(nickname).values()));
        if(nickname.equals(client.getNickname())){ nickname = "your"; } else nickname += "'s";

        displayMessage(String.format("These are the students in %s entrance:", nickname));
        displayMessage(studentFrequency);

    }

    public void displayIslands(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        HashMap<Integer, List<Integer>> islandTileStudentIDs = game.getIslandTilesStudentsIDs();
        HashMap<Integer, Color> archipelagoStudentColors = game.getArchipelagoStudentIDs();
        HashMap<Integer, TowerColor> towerInfo = game.getIslandGroupsOwners();


        StringBuilder toPrint = new StringBuilder("These are the Island Groups and the students they contain:\n");

        for(int islandGroupIdx : islandGroups.keySet()){
            List<Color> students = new ArrayList<>();
            if(towerInfo.get(islandGroupIdx) == null) {
                toPrint.append(String.format("\nIsland group %d contains the following students: \n", islandGroupIdx));
            } else {
                toPrint.append(String.format("\nIsland group %d contains a %s tower and the following students: \n", islandGroupIdx, towerInfo.get(islandGroupIdx)));
            }

            for(int islandTileID : islandGroups.get(islandGroupIdx)) {
                for (int studentID : islandTileStudentIDs.get(islandTileID)) {
                    students.add(archipelagoStudentColors.get(studentID));
                }
                toPrint.append(studentFrequencyString(students));
            }
        }
        displayMessage(toPrint.toString());
    }


    public void displayTables(String nickname){

        if(nickname.equals(client.getNickname())){ nickname = "your"; } else nickname += "'s";

        StringBuilder toPrint = new StringBuilder("These are %s tables:\n");

        for(Color c : Color.values()) {
            toPrint.append(String.format("%s table: %d students\n", c, game.getTableStudentsIDs(nickname, c).size()));
        }

        displayMessage(toPrint.toString());
    }


    public void displayClouds(){
        StringBuilder toPrint = new StringBuilder("These are the clouds and the students they contain:\n\n");
        for(int cloudIdx : cloudMap.keySet()){
            toPrint.append(String.format("Cloud %d contains the following students:\n", cloudIdx));

            toPrint.append(studentFrequencyString(new ArrayList<>(game.getCloudStudentsIDs(cloudMap.get(cloudIdx)).values())));
        }
        displayMessage(toPrint.toString());
    }


    public void requestEndTurn(){}

}
