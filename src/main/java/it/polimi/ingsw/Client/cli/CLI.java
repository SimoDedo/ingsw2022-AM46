package it.polimi.ingsw.Client.cli;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.UI;
import it.polimi.ingsw.GameModel.Board.Player.AssistantCard;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.Phase;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.InputParser;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.util.*;

public class CLI implements UI {

    private final PrintStream output;
    private final Client client;
    private final InputParser parser;
    private final Game game;
    /**
     * maps user-friendly ints to actual cloud IDs
     */
    private final HashMap<Integer, Integer> cloudMap = new HashMap<>();
    // probably useful to create a constants class in utils with this information instead of hard coding it here
    private final HashMap<Integer, String> characterDescriptions = new HashMap<>();



    public CLI(Client client, InputParser parser, Game game){
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = parser;
        this.game = game;
        for(int cloudID : game.getCloudIDs()){
            cloudMap.put(game.getCloudIDs().indexOf(cloudID), cloudID);
        }
        populateCharacterDescriptions();
    }

    private void populateCharacterDescriptions(){

        characterDescriptions.put(1, "In setup, draw 4 Students and place them on this card. EFFECT: Take 1 Student from this card and place it on an Island of your choice. Then, draw a new Student from the Bag and place it on this card.");
        characterDescriptions.put(2, "EFFECT: During this turn, you take control of any number of Professors even if you have the same number of Students as the player who currently controls them.");
        characterDescriptions.put(3, "EFFECT: Choose an Island and resolve the Island as if Mother Nature had ended her movement there. Mother Nature will still move and the Island where she ends her movement will also be resolved.");
        characterDescriptions.put(4, "EFFECT: You may move Mother Nature up to 2 additional Islands than is indicated by the Assistant card you've played.");
        characterDescriptions.put(5, "In Setup, put the 4 No Entry tiles on this card. EFFECT: Place a No Entry tile on an Island of your choice. The first time Mother Nature ends her movement there, put the No Entry tile back onto this card. DO NOT calculate influence on that Island, or place any Towers.");
        characterDescriptions.put(6, "EFFECT: When resolving a Conquering on an Island, Towers do not count towards influence.");
        characterDescriptions.put(7, "In Setup, draw 6 Students and place them on this card.You may take up to 3 Students from this card and replace them with the same number of students from your entrance.");
        characterDescriptions.put(8, "EFFECT: During the influence calculation this turn, you count as having 2 more influence.");
        characterDescriptions.put(9, "EFFECT: Choose a color of Student: during the influence calculation this turn, that color adds no influence.\n");
        characterDescriptions.put(10, "You may exchange up to 2 Students between your Entrance and your Dining Room.");
        characterDescriptions.put(11, "In Setup, draw 4 Students and place them on this card. Take 1 Student  from this card and place it in your Dining Room. Then, draw a new Student from the Bag and place it on this card.");
        characterDescriptions.put(12, "Choose a type of Student: every player (including yourself) must return 3 Students of that type from their Dining Room to the bag. If any player has fewer than 3 Students of that type, return as many Students as they have.");

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

    private Color selectStudentColor(){
        Color color = null;
        displayMessage("Type the color of the student you would like to move:");
        String c = parser.readLine();
        do {
            try {
                if (c.equals("help")) {displayHelp("color"); continue;}
                color = Color.valueOf(c.toUpperCase());

            } catch (IllegalArgumentException e) {
                displayInvalid();
            }
        } while (color == null);

        return color;
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

    // not really used atm but could be cool (used in student color selection)
    public void displayHelp(String context){
        switch (context){
            case "choose cloud" -> displayMessage("Type the number of the cloud you would like to choose.");
            case "move" -> displayMessage("Type the color of the student, followed by either 1 or 2 to specify the type of destination (table or island)." +
                    "In case the destination is an island, you must also specify the ID."); 
            case "play assistant" -> displayMessage("Type the ID of the assistant you would like to play.");
            case "play character" -> displayMessage("Type the ID of the character you would like to purchase.");
            case "color" -> displayMessage("Type the color of the student you would like to select.\nAvailable colors: yellow, blue, green, red, pink");
        }

    }

    public void requestAssistant(){
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.ACTION){
            displayHand();
            displayMessage("Type the number of the assistant you would like to play.");
            if(client.requestAssistant(parser.readNumberFromSelection(game.getCardsLeft(client.getNickname())))){
                displayMessage("Assistant card played successfully, mother nature moved.");
            } else displayMessage("Cannot play assistant at this time. Command discarded.");
        }
    }

    public void requestCloud(){
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.PLANNING){
            displayClouds();
            displayMessage("Type the number of the cloud you would like to choose.");
            if(client.requestCloud(parser.readBoundNumber(0, cloudMap.size() - 1))){
                displayMessage("Cloud selected successfully.");
            } else displayMessage("Cannot choose cloud at this time. Command discarded.");
        } else displayUnavailable();
    }


    public void requestMove(){
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.ACTION){
            displayEntrance(client.getNickname());
            Color color = selectStudentColor();
            displayMessage("Select destination type:\n1: Dining Room\n2: Islands");
            if(parser.readBoundNumber(1, 2) == 2){
                displayArchipelago();
                displayMessage("Select the island group number you would like to place your student in:");
                if(client.requestMove(color, parser.readBoundNumber(0, game.getIslandTilesIDs().size() - 1))){
                    displayMessage("Student moved!");
                } else displayMessage("Illegal movement. Command discarded.");

            } else {
                if (client.requestMove(color)) {
                    displayMessage("Student moved!");
                } else displayMessage("Illegal movement. Command discarded.");
            }
        } else displayUnavailable();
    }



    public void requestCharacter(){
        if(Objects.equals(game.getCurrentPlayer(), client.getNickname())) {
            displayCharacters();
            if(client.requestCharacter(parser.readNumberFromSelection(game.getCurrentCharacterIDs()))){
                displayMessage("Character hired successfully.");
            } else {
                displayMessage("Unable to hire character. Do you have enough coins?");
            }

        }
    }


    public void displayUnavailable(){
        displayMessage("You cannot execute this command at this time.");
    }

    public void displayInvalid(){
        displayMessage("Unrecognized command - please type help for a list of available commands.");
    }




    public void standings(){
        displayArchipelago();
        displayClouds();
        displayCharacters();
        for(String nickname : game.getPlayerOrder()) {
            displayEntrance(nickname);
            displayTables(nickname);
        }
        displayHand();
    }


    public void displayEntrance(String nickname){

        if(nickname.equals(client.getNickname())){ nickname = "your"; } else nickname += "'s";

        StringBuilder toPrint = new StringBuilder(String.format("These are the students in %s entrance:", nickname));
        toPrint.append(studentFrequencyString(new ArrayList<>(game.getEntranceStudentsIDs(nickname).values())));

        displayMessage(toPrint.toString());

    }


    public void displayArchipelago(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        HashMap<Integer, List<Integer>> islandTileStudentIDs = game.getIslandTilesStudentsIDs();
        HashMap<Integer, Color> archipelagoStudentColors = game.getArchipelagoStudentIDs();
        HashMap<Integer, TowerColor> towerInfo = game.getIslandGroupsOwners();
        HashMap<Integer, Integer> noEntryTiles = game.getNoEntryTiles();


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
            if(noEntryTiles.containsKey(islandGroupIdx)){
                toPrint.append("There is also a no entry tile present in this island group.");
            }

        }
        displayMessage(toPrint.toString());
    }

    //TODO display coins
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

    //TODO display cost and overcharge
    public void displayCharacters(){
        StringBuilder toPrint = new StringBuilder("These are the characters which have been picked for this game:");
        for(int ID : game.getCurrentCharacterIDs())
            toPrint.append(String.format("Character %d: %s", ID, characterDescriptions.get(ID)));
        if(game.getCurrentCharacterIDs().size() > 0) displayMessage(toPrint.toString());
    }

    public void displayHand(){
        StringBuilder toPrint = new StringBuilder("These are the Assistant cards still in your hand:");
        for(int c : game.getCardsLeft(client.getNickname())){
            toPrint.append(String.format("Card %d with move power %d", c, (c + 1)/2));
        }

        displayMessage(toPrint.toString());
    }

    public void requestEndTurn(){}

}
