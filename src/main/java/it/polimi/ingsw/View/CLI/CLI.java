package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.CommandString;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.InputParser;
import it.polimi.ingsw.View.Client;
import it.polimi.ingsw.View.UI;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Utils.AnsiColors.*;

public class CLI implements UI {
    private final PrintStream output;
    private final InputParser parser;
    private ObservableByClient game;

    private Scanner sysIn;

    private String nickname;

    private final Client client;

    private LinkedHashSet<String> infoCommandList;
    private LinkedHashSet<String> gameCommandList;

    private HashMap<Color, String> colorMapping;

    private final Object lock;
    private boolean serverResponse;

    public CLI(Client client) {
        lock = new Object();
        serverResponse = false;
        sysIn = new Scanner(System.in);
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = new InputParser();
        infoCommandList = new LinkedHashSet<>();
        gameCommandList = new LinkedHashSet<>();
        mapColors();
        populateCharacterDescriptions();
    }

    @Override
    public void update(ObservableByClient game) {
        this.game = game;
    }

    public void askTryConnecting(){
        System.out.println("Press enter to try connecting to default server:");
        sysIn.nextLine();
    }

    public Map<String, String> askServerInfo(){
        Map<String, String> info = new HashMap<>();
        info.put("IP", askServerIP());
        info.put("port", String.valueOf( askServerPort()));
        return info;
    }

    private String askServerIP(){
        System.out.println("Choose server IP:");
        String s = sysIn.nextLine();
        clearScreen();
        return s;
    }

    private int askServerPort(){
        int port = -1;
        while (port<0 || port>65535){
            System.out.println("Choose server port:");
            String s = sysIn.nextLine();
            try{
                port = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            if(port <=0 || port>65535) {
                System.err.println("Please select a number between 0 and 65535");
            }
        }
        return port;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String askNickname(){
        System.out.println("Input nickname:");
        String s = sysIn.nextLine();
        clearScreen();
        return s;
    }

    public void askGameSettings(){
        int numOfPlayers = 0;
        while (numOfPlayers < 2 || numOfPlayers > 4){
            System.out.println("Select how many player will participate in this game [2,3 or 4]:");
            String s = sysIn.nextLine();
            try{
                numOfPlayers = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            if(numOfPlayers < 2 || numOfPlayers > 4){
                System.out.println("Please select 2,3 or 4.");
            }
        }
        int gameModeChoice = 0;
        while (gameModeChoice < 1 || gameModeChoice > 2){
            System.out.println("Select game mode:\n1.Normal\n2.Expert");
            String s = sysIn.nextLine();
            try{
                gameModeChoice = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            if(gameModeChoice < 1 || gameModeChoice > 2){
                System.out.println("Invalid selection! Please select Normal or Expert.");
            }
        }
        client.sendUserAction(new GameSettingsUserAction(nickname,
                numOfPlayers,
                Arrays.stream(GameMode.values()).toList().get(gameModeChoice - 1)));
    }

    public void askTowerColor(int numOfPlayers){
        int towerChoice = 0;
        int maxChoice;
        String request;
        if (numOfPlayers == 3) {
            maxChoice = 3;
            request = "Choose your team color:\n1.White\n2.Black\n3.Grey";
        }
        else {
            maxChoice = 2;
            request = "Choose your team color:\n1.White\n2.Black";
        }
        while (towerChoice < 1 || towerChoice > maxChoice) {
            System.out.println(request);
            String s = sysIn.nextLine();
            try{
                towerChoice = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            if(towerChoice < 1 || towerChoice > maxChoice)
                System.out.println("Please choose a valid team color.");
        }
        client.sendUserAction( new TowerColorUserAction(nickname, Arrays.stream(TowerColor.values()).toList().get(towerChoice - 1)));
    }

    public void askWizard(){
        int wizardChoice = 0;
        while (wizardChoice < 1 || wizardChoice > 4) {
            System.out.println("Choose your wizard:\n1.Mage\n2.King\n3.Witch\n4.samurai");
            String s = sysIn.nextLine();
            try{
                wizardChoice = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            if(wizardChoice < 1 || wizardChoice > 4)
                System.out.println("Please choose a valid wizard.");
        }
        client.sendUserAction(new WizardUserAction(nickname, Arrays.stream(WizardType.values()).toList().get(wizardChoice - 1)));
    }

    @Override
    public void showText(String text) {
        System.out.println(text+"\n");
    }

    public void showInfo(String info){
        System.out.println(YELLOW_INFO + info + RESET + "\n");
    }

    @Override
    public void showError(String error) {
        System.out.println(RED + error +"\nPlease retry." + RESET);
    }

    private void printDivider(){
        output.println("--------------------------------------------------------------------------------------------");
    }

    private void clearScreen() {
        printDivider();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void reset(){
    }


    //Pietro


    /**
     * maps user-friendly ints to actual cloud IDs
     */
    // probably useful to create a constants class in utils with this information instead of hard coding it here
    private final HashMap<Integer, String> characterDescriptions = new HashMap<>();



    public CLI(Client client, InputParser parser, Game game, Object lock){
        this.lock = lock;
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = parser;
        this.game = game;
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
        HashMap<Color, Integer> frequencyMap = new LinkedHashMap<>();
        for(Color c : students){
            if(frequencyMap.containsKey(c)){
                frequencyMap.put(c, frequencyMap.get(c) + 1);
            } else {
                frequencyMap.put(c, 1);
            }
        }
        StringBuilder colorFrequency = new StringBuilder();
        for(Color c : frequencyMap.keySet()){
            colorFrequency.append(String.format(colorMapping.get(c) + "%d %s " + RESET, frequencyMap.get(c), c));
        }
        return colorFrequency.toString();
    }

    private Color selectStudentColor(){
        Color color = null;
        displayMessage("Type the color of the student you would like to move:");
        do {
            String c = parser.readLine();
            try {
                if (c.equals("help")) {displayHelp("color"); continue;}
                color = Color.valueOf(c.toUpperCase());

            } catch (IllegalArgumentException e) {
                displayInvalid();
            }
        } while (color == null);

        return color;
    }

    public void enableCommand(Command command){
        if(command.isGameCommand())
            gameCommandList.add(command.getCommandToWrite());
        else
            infoCommandList.add(command.getCommandToWrite());
    }

    public void disableCommand(Command command){
        infoCommandList.remove(command.getCommandToWrite());
        gameCommandList.remove(command.getCommandToWrite());
    }


    public void parseCommand(){
        boolean quit = false;
        while (!quit){

            String command = parser.readLine();
            clearScreen();


            if(infoCommandList.contains(command.toLowerCase())){
                switch (command){
                    case CommandString.help -> displayHelp();
                    case CommandString.table -> standings();
                    case CommandString.cards -> displayAvailableCards();
                    case CommandString.order -> displayCurrentOrder();
                    case CommandString.quit -> quit = true;
                    default-> displayInvalid();
                }
                displayAvailableCommands();
            }

            else if(gameCommandList.contains(command.toLowerCase())){
                switch (command){
                    case CommandString.assistant -> requestAssistant();
                    case CommandString.move -> requestMoveFromEntrance();
                    case CommandString.motherNature -> requestMotherNature();
                    case CommandString.cloud -> requestCloud();
                    case CommandString.character -> requestCharacter();
                    case CommandString.endTurn -> requestEndTurn();
                    default-> { displayInvalid(); displayAvailableCommands();}
                }

                synchronized (lock){
                    clearScreen();
                    displayMessage("Waiting server response...");
                    serverResponse = false;
                    while(!serverResponse){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            showError("An error occurred");
                            System.exit(-1);
                        }
                    }
                    serverResponse = false;
                }
            }
            else{
                displayInvalid();
                displayAvailableCommands();
            }
        }
        System.exit(0);
    }


    public void notifyServerResponse(){
        synchronized (lock){
            serverResponse = true;
            lock.notifyAll();
        }
    }


    public void displayLogin() {
        displayMessage("Placeholder welcome message");

    }


    public void displayBoard() {
        standings();
    }


    public void displayMessage(String message) {
        message = message + "\n";
        System.out.println(message);
    }

    public void displayMessage(String message, String color) {
        message = color + message + RESET + "\n";
        System.out.println(message);
    }


    public void requestLogin(){
        displayMessage("Please input a nickname:");
        String nickname = parser.readLine();
        if(client.requestLogin(nickname)){
            displayMessage("Nickname successfully set.");
        } else displayMessage("Unable to set nickname at this time. Please try again");

    }


    public void requestTowerColor(){
        displayMessage("Please choose a tower color from the following: " + Arrays.toString(game.getAvailableTowerColors().toArray()));
        if(client.requestTowerColor(parser.readLineFromSelection(game.getAvailableTowerColors().stream().map(o->o.toString().toLowerCase()).collect(Collectors.toList())))){
            displayMessage("Tower color selected successfully");
        } else displayMessage("Cannot choose selected tower color at this time. Command discarded.");
    }

    public void requestGameMode(){
        displayMessage("Please type 1 for normal or 2 for hard.");
        if(client.requestGameMode(parser.readBoundNumber(1, 2))){
            displayMessage("Game mode successfully selected");
        } else displayMessage("Cannot choose selected game mode at this time. Command discarded.");
    }

    public void requestPlayerNumber(){
        displayMessage("Please type the number of players this game will have.");
        if(client.requestGameMode(parser.readBoundNumber(2, 4))){
            displayMessage("Player number successfully selected.");
        } else displayMessage("Cannot choose selected number of players at this time. Command discarded.");

    }


    public void requestWizard (){
        displayMessage("Please choose a type of wizard from the following: " + Arrays.toString(game.getAvailableWizards().toArray()));
        if(client.requestWizard(parser.readLineFromSelection(game.getAvailableWizards().stream().map(o->o.toString().toLowerCase()).collect(Collectors.toList())))){
            displayMessage("Wizard selected successfully");
        } else displayMessage("Cannot choose selected wizard at this time. Command discarded.");

    }


    public void requestAssistant(){
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.PLANNING){
            displayHand(this.nickname);
            displayMessage("Type the number of the assistant you would like to play.");
            if(client.requestAssistant(parser.readNumberFromSelection(game.getCardsLeft(client.getNickname())))){
                displayMessage("Assistant card played successfully.");
            } else displayMessage("Cannot play assistant at this time. Command discarded.");
        }
    }


    /**
     *
     */
    public void requestMoveFromEntrance(){
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.ACTION){
            displayEntrance(client.getNickname());
            Color color = selectStudentColor();
            HashMap<Integer, List<Integer>> islandTileIDs = game.getIslandTilesIDs();
            HashMap<Integer, Color> studentIDs = game.getEntranceStudentsIDs(client.getNickname());

            displayMessage("Select destination type:\n1: Islands\n2: Dining Room");
            if(parser.readBoundNumber(1, 2) == 1){
                displayArchipelago();
                displayMessage("Select the island group number you would like to place your student in:");
                if(client.requestMove(color, islandTileIDs.get(parser.readBoundNumber(0, islandTileIDs.size() - 1)).get(0))){
                    displayMessage("Student moved!");
                } else displayMessage("Illegal movement. Command discarded.");

            } else {
                if (client.requestMove(color, game.getTableIDs(client.getNickname()).get(color))) {
                    displayMessage("Student moved!");
                } else displayMessage("Illegal movement. Command discarded.");
            }
        } else displayUnavailable();
    }

    public void requestMotherNature(){
        displayArchipelago();
        HashMap<Integer, List<Integer>> islandTileIDs = game.getIslandTilesIDs();
        displayMessage("Select the island group number you would like to move mother nature to:");
        if(client.requestMotherNature(islandTileIDs.get(parser.readBoundNumber(0, islandTileIDs.size() - 1)).get(0))){
            displayMessage("Mother nature moved!");
        } else displayMessage("Illegal movement. Command discarded.");
    }


    public void requestCloud(){
        List<Integer> clouds = game.getCloudIDs();
        if(game.getCurrentPlayer().equals(client.getNickname()) && game.getCurrentPhase() == Phase.ACTION){
            displayClouds();
            displayMessage("Type the number of the cloud you would like to choose.");
            if(client.requestCloud(clouds.get(parser.readBoundNumber(0, game.getCloudIDs().size() - 1)))){
                displayMessage("Cloud selected successfully.");
            } else displayMessage("Cannot choose cloud at this time. Command discarded.");
        } else displayUnavailable();
    }




    public void requestCharacter(){
        if(Objects.equals(game.getCurrentPlayer(), client.getNickname())) {
            displayCharacters();
            if(client.requestCharacter(parser.readNumberFromSelection(game.getDrawnCharacterIDs()))){
                displayMessage("Character hired successfully.");
            } else {
                displayMessage("Unable to hire character. Do you have enough coins?");
            }

        }
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

    // not really used atm but could be cool (I lied it's used in student color selection)
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

    public void displayUnavailable(){
        displayMessage("You cannot execute this command at this time.");
    }

    public void displayInvalid(){
        displayMessage("Unrecognized command - please type help for a list of available commands.");
    }



    public void standings(){
        clearScreen();
        displayPlayersInfo();
        System.out.print("\033[1A"); //Move cursor up to not leave empty line
        displayTurnInfo();
        output.println("TABLE:");
        displayArchipelago();
        displayClouds();
        if (game.getGameMode() == GameMode.EXPERT)
            displayCharacters();
        for(String nickname : game.getPlayers()) {
            displayEntrance(nickname);
            System.out.print("\033[1A"); //Move cursor up to not leave empty line
            displayTables(nickname);
        }
        displayPlayedCards();
        //displayHand(); //screen gets too busy
    }

    public void displayTurnInfo(){
        StringBuilder toPrint = new StringBuilder("Phase: ");
        toPrint.append(game.getCurrentPhase());
        toPrint.append(" | Turn of: ");
        toPrint.append(game.getCurrentPlayer());
        displayMessage(toPrint.toString());
    }


    public void displayEntrance(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(client.getNickname())){ nickToWrite = "your"; } else nickToWrite += "'s";

        StringBuilder toPrint = new StringBuilder(String.format("These are the students in %s ENTRANCE:\n", nickToWrite));
        toPrint.append(studentFrequencyString(new ArrayList<>(game.getEntranceStudentsIDs(nickname).values())));

        displayMessage(toPrint.toString());
    }


    public void displayArchipelago(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        HashMap<Integer, List<Integer>> islandTileStudentIDs = game.getIslandTilesStudentsIDs();
        HashMap<Integer, Color> archipelagoStudentColors = game.getArchipelagoStudentIDs();
        HashMap<Integer, TowerColor> towerInfo = game.getIslandGroupsOwners();
        HashMap<Integer, Integer> noEntryTiles = game.getNoEntryTiles();


        StringBuilder toPrint = new StringBuilder("These are the ISLANDS and the students they contain:\n");

        for(int islandGroupIdx : islandGroups.keySet()){
            List<Color> students = new ArrayList<>();
            toPrint.append(String.format("\nIsland group %d: ", islandGroupIdx));

            for(int islandTileID : islandGroups.get(islandGroupIdx)) {
                for (int studentID : islandTileStudentIDs.get(islandTileID)) {
                    students.add(archipelagoStudentColors.get(studentID));
                }
            }
            toPrint.append(studentFrequencyString(students));

            if(towerInfo.get(islandGroupIdx) != null) {
                toPrint.append(String.format("%d %s towers ", islandGroups.get(islandGroupIdx).size() , towerInfo.get(islandGroupIdx)));
            }
            if(game.getMotherNatureIslandGroupIdx() == islandGroupIdx) {
                toPrint.append(BRIGHT_RED + "Mother Nature " + RESET);
            }
            if(noEntryTiles.get(islandGroupIdx) > 0){
                toPrint.append(String.format("%d no entry tiles ", noEntryTiles.get(islandGroupIdx)));
            }

        }
        displayMessage(toPrint.toString());
    }

    //TODO display coins
    public void displayTables(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(client.getNickname())){ nickToWrite = "your"; } else nickToWrite += "'s";

        StringBuilder toPrint = new StringBuilder(String.format("These are %s TABLES:\n", nickToWrite));

        for(Color c : Color.values()) {
            toPrint.append(String.format(colorMapping.get(c) + "%s: %d " + RESET, c, game.getTableStudentsIDs(nickname, c).size()));
            if(game.getProfessorsOwner().get(c) != null && game.getProfessorsOwner().get(c).equals(nickname))
                toPrint.append(colorMapping.get(c) + "Professor " + RESET);
        }

        displayMessage(toPrint.toString());
    }


    public void displayClouds(){
        StringBuilder toPrint = new StringBuilder("These are the CLOUDS and the students they contain:");
        List<Integer> clouds = game.getCloudIDs();
        for(int cloudID : clouds){
            toPrint.append(String.format("\nCloud %d: ", clouds.indexOf(cloudID)));

            toPrint.append(studentFrequencyString(new ArrayList<>(game.getCloudStudentsIDs(cloudID).values())));
        }
        displayMessage(toPrint.toString());
    }

    //TODO display cost and overcharge
    public void displayCharacters(){
        StringBuilder toPrint = new StringBuilder("These are the CHARACTERS which have been picked for this game:\n");
        for(int ID : game.getDrawnCharacterIDs())
            toPrint.append(String.format("Character %d: %s", ID, characterDescriptions.get(ID)));
        if(game.getDrawnCharacterIDs().size() > 0) displayMessage(toPrint.toString());
    }

    public void displayHand(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(client.getNickname())){ nickToWrite = "your"; } else nickToWrite += "'s";
        StringBuilder toPrint = new StringBuilder("These are the ASSISTANTS still in " + nickToWrite + " hand:\n");
        for(int c : game.getCardsLeft(nickname)){
            toPrint.append(String.format("\nCard %d with move power %d", c, (c + 1)/2));
        }
        displayMessage(toPrint.toString());
    }

    public void displayPlayersInfo(){
        HashMap<String, TowerColor> towers = game.getPlayerTeams();
        HashMap<String, WizardType> wizards = game.getPlayerWizard();

        StringBuilder toPrint = new StringBuilder("Players: ");
        for(String nickname : game.getPlayerOrder()){
            toPrint.append(nickname + " - Tower " + towers.get(nickname) + ", Wizard " + wizards.get(nickname) + " | ");
        }
        toPrint.deleteCharAt(toPrint.lastIndexOf("| "));
        displayMessage(toPrint.toString());
    }

    public void displayCurrentOrder(){
        StringBuilder toPrint = new StringBuilder("This is the current " + game.getCurrentPhase() + " phase order:\n");
        int i = 1;
        for(String nickname : game.getPlayerOrder()){
            toPrint.append(i+"-"+nickname+" ");
            i++;
        }
        displayMessage(toPrint.toString());
    }

    public void displayPlayedCards(){
        StringBuilder toPrint = new StringBuilder("These are the ASSISTANTS currently played:\n");
        for(String nickname : game.getCardsPlayedThisRound().keySet()){
            toPrint.append(nickname+": " + game.getCardsPlayedThisRound().get(nickname) +"\n");
        }
        displayMessage(toPrint.toString());
    }

    public void displayAvailableCards(){
        for (String nickname : game.getPlayers()){
            displayHand(nickname);
        }
    }

    public void displayAvailableCommands(){
        StringBuilder toPrint = new StringBuilder(
                "These are the available " + CYAN + "info " + RESET + "and " + RED + "game " + RESET + "commands right now:\n");
        for(String command : infoCommandList){
            toPrint.append(CYAN + command + RESET + ", " );
        }
        for(String command : gameCommandList){
            toPrint.append(RED + command + RESET + ", ");
        }
        toPrint.deleteCharAt(toPrint.lastIndexOf(", "));
        displayMessage(toPrint.toString());
    }

    public void requestEndTurn(){}

    private void mapColors(){
        colorMapping = new HashMap<>();
        colorMapping.put(Color.RED, RED);
        colorMapping.put(Color.PINK, MAGENTA);
        colorMapping.put(Color.YELLOW, YELLOW);
        colorMapping.put(Color.GREEN, GREEN);
        colorMapping.put(Color.BLUE, CYAN);
    }
}
