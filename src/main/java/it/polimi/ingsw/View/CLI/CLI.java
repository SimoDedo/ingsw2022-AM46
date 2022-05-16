package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.InputParser;

import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.View.Client;
import it.polimi.ingsw.View.UI;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Utils.AnsiColors.*;

public class CLI implements UI {
    private final Client client;
    private String nickname;

    private final PrintStream output;
    private final InputParser parser;
    private ObservableByClient game;

    private final LinkedHashSet<Command> infoCommandList;
    private final LinkedHashSet<Command> gameCommandList;

    private HashMap<Color, String> colorMapping; //TODO: consider moving in ANSI COLOR class as static method

    private final Object lock;
    private boolean serverResponse;
    private boolean gameStarted;

    private final List<String> infoQueue;
    private final List<String> errorQueue;

    private final ExecutorService setupOperation;

    public CLI(Client client) {
        lock = new Object();
        setupOperation = Executors.newSingleThreadExecutor();
        serverResponse = false;
        gameStarted = false;
        output = new PrintStream(System.out);
        this.client = client;
        this.parser = new InputParser();
        infoCommandList = new LinkedHashSet<>();
        gameCommandList = new LinkedHashSet<>();
        infoQueue = new ArrayList<>();
        errorQueue = new ArrayList<>();
        mapColors();
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void update(ObservableByClient game) {
        this.game = game;
    }

    @Override
    public void startGame(){
        gameStarted = true;
        boolean quit = false;
        while (!quit){
            String commandString = parser.readLine();
            clearScreen();

            Command command = mapCommand(commandString);
            if(command != null){
                if(infoCommandList.contains(command)){
                    switch (command){
                        case HELP -> displayHelp();
                        case TABLE -> standings();
                        case CARDS -> displayAvailableCards();
                        case ORDER -> displayCurrentOrder();
                        case CHARACTER_INFO -> displayCharactersDetailed();
                        case QUIT -> quit = true;
                        default-> displayInvalid();
                    }
                    if(!quit)
                        displayAvailableCommands();
                }

                else if(gameCommandList.contains(command)){
                    switch (command){
                        case ASSISTANT -> requestAssistant();
                        case MOVE -> requestMoveFromEntrance();
                        case MOTHER_NATURE -> requestMotherNature();
                        case CLOUD -> requestCloud();
                        case CHARACTER -> requestCharacter();
                        case ABILITY -> requestCharacterAbility();
                        case END_TURN -> requestEndTurn();
                        default-> displayInvalid();
                    }
                    waitForServerResponse();
                }
                else{ //Shouldn't reach here if command mapping stays as it is, if it changes it is needed
                    displayInvalid();
                    displayAvailableCommands();
                }
            }
            else{
                displayInvalid();
                displayAvailableCommands();
            }
        }
        System.exit(0);
    }

    public void waitForServerResponse(){
        synchronized (lock){
            clearScreen();
            displayMessage("Waiting server response...");
            serverResponse = false;
            while(!serverResponse){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    displayError("An error occurred", true);
                    System.exit(-1);
                }
            }
            serverResponse = false;
        }
    }
    @Override
    public void notifyServerResponse(boolean gameStarted){
        if(gameStarted){
            clearScreen();
            standings();
            displayInfoQueue();
            displayErrorQueue();
            displayAvailableCommands();
        }
        synchronized (lock){
            serverResponse = true;
            lock.notifyAll();
        }
    }

    @Override
    public void updateCommands(List<Command> toDisable, List<Command> toEnable){
        for(Command command : toDisable){
            disableCommand(command);
        }
        for (Command command : toEnable){
            enableCommand(command);
        }
    }

    private void enableCommand(Command command){
        if(command.isGameCommand())
            gameCommandList.add(command);
        else
            infoCommandList.add(command);
    }

    private void disableCommand(Command command){
        infoCommandList.remove(command);
        gameCommandList.remove(command);
    }

    private Command mapCommand(String commandGiven){
        for(Command command : infoCommandList){
            if(command.getCommandToWrite().equals(commandGiven))
                return command;
        }
        for(Command command : gameCommandList){
            if(command.getCommandToWrite().equals(commandGiven))
                return command;
        }
        return null;
    }

    @Override
    public Map<String, String> requestServerInfo(String defaultIP, int defaultPort){
        displayWelcome();
        displayErrorQueue();
        Map<String, String> info = new HashMap<>();
        info.put("IP", requestServerIP(defaultIP));
        info.put("port", String.valueOf( requestServerPort(defaultPort)));
        return info;
    }

    private int requestServerPort(int defaultPort){
        int port = -1;
        while (port<0 || port>65535){
            displayMessage(String.format("Choose server port [press enter for %d]:", defaultPort));
            String s = parser.readLine();
            try{
                port = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            if(s.equals(""))
                port = defaultPort;
            clearScreen();
            if(port <=0 || port>65535) {
                System.err.println("Please select a number between 1 and 65535");
            }
        }
        return port;
    }

    private String requestServerIP(String defaultIP){
        displayMessage(String.format("Choose server IP [press enter for %s]:", defaultIP));
        String s = parser.readLine();
        if(s.equals(""))
            s = defaultIP;
        clearScreen();
        return s;
    }

    @Override
    public String requestNickname() {
        displayErrorQueue();
        displayMessage("Choose a nickname:");
        String nickname = parser.readLine();
        clearScreen();
        while (nickname == null || nickname.equals("")
                ||! Character.isLetterOrDigit(nickname.charAt(0)) || nickname.length() > 50){ //Check also done server side
            displayMessage("Nickname should start with a letter or number and should be less than 50 characters long! Choose another nickname:");
            nickname = parser.readLine();
            clearScreen();
        }

        return nickname;
    }

    @Override
    public void requestGameSettings(){
        displayErrorQueue();
        setupOperation.execute(() ->{
            GameMode gameMode = requestGameMode();
            clearScreen();
            int numOfPlayers = requestPlayerNumber();
            clearScreen();
            UserAction gameSettingsRequest = new GameSettingsUserAction(nickname, numOfPlayers, gameMode);

            client.sendUserAction(gameSettingsRequest);
        });
    }

    private GameMode requestGameMode(){
        displayMessage("Select a game mode:\n1 Normal\n2 Expert");
        return GameMode.values()[parser.readBoundNumber(1, 2) - 1];
    }

    private int requestPlayerNumber(){
        displayMessage("Select how many players will participate in the game [2,3 or 4]:");
        return parser.readBoundNumber(2, 4);
    }

    @Override
    public void requestTowerColor(ObservableByClient game){
        update(game);
        displayErrorQueue();
        setupOperation.execute(() ->{
            List<TowerColor> available = this.game.getAvailableTowerColors();
            int sel = 1;

            displayPlayersInfo();
            StringBuilder toPrint = new StringBuilder("Please choose a tower color:\n");
            for (TowerColor towerColor : available){
                toPrint.append(sel).append(": ").append(towerColor).append("\n");
                sel++;
            }
            displayMessage(toPrint.toString());
            TowerColor selection = available.get(parser.readBoundNumber(1, available.size()) - 1);

            clearScreen();
            UserAction towerColorRequest = new TowerColorUserAction(nickname, selection);
            client.sendUserAction(towerColorRequest);
        });
    }

    @Override
    public void requestWizard(ObservableByClient game){
        update(game);
        displayErrorQueue();
        setupOperation.execute(() -> {
            List<WizardType> available = this.game.getAvailableWizards();
            int sel = 1;

            displayPlayersInfo();
            StringBuilder toPrint = new StringBuilder("Please choose your wizard:\n");
            for (WizardType wizardType : available){
                toPrint.append(sel).append(": ").append(wizardType).append("\n");
                sel++;
            }
            displayMessage(toPrint.toString());
            WizardType selection = available.get(parser.readBoundNumber(1, available.size()) - 1);

            clearScreen();
            UserAction wizardRequest = new WizardUserAction(nickname, selection);
            client.sendUserAction(wizardRequest);
        });
    }

    public void requestAssistant(){
        displayHand(this.nickname);
        displayPlayedCards();
        displayMessage("Type the number of the assistant you would like to play.");

        int assistantIDSelection = parser.readNumberFromSelection(game.getCardsLeft(nickname));
        UserAction assistantRequest = new PlayAssistantUserAction(nickname, assistantIDSelection);

        client.sendUserAction(assistantRequest);
    }

    public void requestMoveFromEntrance(){
        HashMap<Integer, Color> studentIDs = game.getEntranceStudentsIDs(nickname);

        displayEntrance(nickname);
        displayMessage("Select a student from your entrance:");
        int studentID = selectStudentFromContainer(RequestParameter.STUDENT_ENTRANCE, -1);
        int destinationID;

        displayMessage("Select destination type:\n1: Islands\n2: Dining Room");
        if(parser.readBoundNumber(1, 2) == 1){
            displayArchipelago();
            displayMessage("Select the island group number you would like to place your student in:");
            destinationID = selectIslandTileFromIdx();
        } else {
            destinationID = game.getTableIDs(nickname).get(studentIDs.get(studentID));
        }

        UserAction moveStudentRequest = new MoveStudentUserAction(nickname, studentID, destinationID);

        client.sendUserAction(moveStudentRequest);
    }

    public void requestMotherNature(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        int movePower = game.getActualMovePower(nickname);

        displayArchipelago();
        displayMessage(String.format("Type the number of spaces mother nature should move (max %d):", movePower));

        int steps = parser.readBoundNumber(1, movePower);
        int dstIslandGroup = game.getMotherNatureIslandGroupIdx() + steps;
        dstIslandGroup = dstIslandGroup < islandGroups.size() ?
                dstIslandGroup : dstIslandGroup - islandGroups.size();
        int dstIslandID = islandGroups.get(dstIslandGroup).get(0);

        UserAction moveMotherNatureRequest = new MoveMotherNatureUserAction(nickname, dstIslandID);

        client.sendUserAction(moveMotherNatureRequest);
    }

    public void requestCloud(){
        List<Integer> clouds = game.getCloudIDs();
        displayClouds();
        displayMessage("Type the number of the cloud you would like to choose.");

        int cloudSelection = clouds.get(parser.readBoundNumber(1, game.getCloudIDs().size()) - 1);
        UserAction cloudRequest = new TakeFromCloudUserAction(nickname, cloudSelection);

        client.sendUserAction(cloudRequest);
    }

    public void requestCharacter(){
        displayCharactersDetailed();
        displayMessage("Select the number of the character you want to activate!");

        int characterID = parser.readNumberFromSelection(game.getDrawnCharacterIDs());
        UserAction characterRequest = new UseCharacterUserAction(nickname, characterID);

        client.sendUserAction(characterRequest);
    }

    public void requestCharacterAbility(){
        List<RequestParameter> requestedParameters = game.getCurrentRequestParameters();
        int characterID = game.getActiveCharacterID();
        List<Integer> parameters = new ArrayList<>();

        displayMessage("To activate the ability, select the requested parameters:");
        displayMessage(characterStringDetailed(game.getActiveCharacterID()));

        for(RequestParameter requestParameter : requestedParameters){
            switch (requestParameter){
                case STUDENT_CARD -> {
                    displayMessage("Select a student from the character you activated:");
                    int charStudentID = selectStudentFromContainer(RequestParameter.STUDENT_CARD, characterID);
                    parameters.add(charStudentID);
                }
                case ISLAND -> {
                    displayArchipelago();
                    displayMessage("Select an island:");
                    int destinationID = selectIslandTileFromIdx();
                    parameters.add(destinationID);
                }
                case STUDENT_ENTRANCE -> {
                    displayEntrance(nickname);
                    displayMessage("Select a student from your entrance:");
                    int entranceStudentID = selectStudentFromContainer(RequestParameter.STUDENT_ENTRANCE, -1);
                    parameters.add(entranceStudentID);
                }
                case STUDENT_DINING_ROOM -> {
                    displayDiningRoom(nickname);
                    displayMessage("Select a student from your dining room:");
                    int drStudentID = selectStudentFromContainer(RequestParameter.STUDENT_DINING_ROOM, -1);
                    parameters.add(drStudentID);
                }
                case COLOR -> {
                    for(String player : game.getPlayers())
                        displayPlayerBoard(player);
                    displayMessage("Please choose a color:");
                    int colorSelection = selectColor().ordinal();
                    parameters.add(colorSelection);
                }
            }
        }
        UserAction moveStudentRequest = new UseAbilityUserAction(nickname, parameters);
        client.sendUserAction(moveStudentRequest);
    }

    public void requestEndTurn(){
        displayMessage("Would you to play a character before ending your turn? y/n");

        String endTurnSelection = parser.readLineFromSelection(new ArrayList<>(Arrays.asList("y", "n")));

        if(endTurnSelection.equals("n")){
            UserAction endTurnRequest = new EndTurnUserAction(nickname);
            client.sendUserAction(endTurnRequest);
        }
    }

    @Override
    public void displayMessage(String message) {
        message = message + "\n";
        output.println(message);
    }

    @Override
    public void displayInfo(String info){
        if(!gameStarted)
            output.println(YELLOW_INFO + info + RESET + "\n");
        else
            infoQueue.add(YELLOW_INFO + info + RESET + "\n");
    }

    private void displayInfoQueue(){
        for(String info : infoQueue)
            output.println(info);
        infoQueue.clear();
    }

    @Override
    public void displayError(String error, boolean isUrgent) {
        if(isUrgent){
            System.out.println(RED + error + RESET);
        }
        else
            errorQueue.add(RED + error +"\nPlease retry.\n" + RESET);
    }

    private void displayErrorQueue(){
        for(String error : errorQueue)
            output.println(error);
        errorQueue.clear();
    }

    @Override
    public void displayBoard(ObservableByClient game, UserActionType actionTaken) {
        update(game);
    }

    public void displayWelcome() {
        clearScreen();
        displayMessage("WELOCME TO ERIANTYS!\n");
    }

    public void displayHelp(){
        StringBuilder toPrint = new StringBuilder();
        toPrint.append("The available ").append(CYAN).append("info").append(RESET).append(" commands are:\n\n");
        for(Command command : infoCommandList){
            toPrint.append(CYAN).append(command.getCommandToWrite()).append(RESET).append(": ").append(command.getHelp());
            toPrint.append("\n");
        }
        toPrint.append("\nThe available ").append(RED).append("game").append(RESET).append(" commands are:\n\n");
        for(Command command : gameCommandList){
            toPrint.append(RED).append(command.getCommandToWrite()).append(RESET).append(": ").append(command.getHelp());
            toPrint.append("\n");
        }
        displayMessage(toPrint.toString());
    }

    public void displayInvalid(){
        displayMessage("Unrecognized command - please type help for a list of available commands.");
    }

    public void standings(){
        displayPlayersInfo();
        output.print("\033[1A"); //Move cursor up to not leave empty line
        displayTurnInfo();
        displayMessage("TABLE:");
        displayArchipelago();
        displayClouds();
        if (game.getGameMode() == GameMode.EXPERT)
            displayCharactersShort();
        for(String nickname : game.getPlayers()) {
            displayPlayerBoard(nickname);
        }
        displayTowersLeft();
        displayPlayedCards();
    }

    public void displayTurnInfo(){
        String toPrint = "Phase: " + game.getCurrentPhase() +
                " | Turn of: " +
                game.getCurrentPlayer();
        displayMessage(toPrint);
    }

    public void displayArchipelago(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        HashMap<Integer, List<Integer>> islandTileStudentIDs = game.getIslandTilesStudentsIDs();
        HashMap<Integer, Color> archipelagoStudentColors = game.getArchipelagoStudentIDs();
        HashMap<Integer, TowerColor> towerInfo = game.getIslandGroupsOwners();
        HashMap<Integer, Integer> noEntryTiles = game.getNoEntryTilesArchipelago();


        StringBuilder toPrint = new StringBuilder("ISLANDS:");

        for(int islandGroupIdx : islandGroups.keySet()){
            List<Color> students = new ArrayList<>();
            toPrint.append(String.format("\nIsland %d ", islandGroupIdx + 1));
            if(islandGroupIdx + 1 < 10)
                toPrint.append(" ");
            toPrint.append("(");
            toPrint.append("X".repeat(islandGroups.get(islandGroupIdx).size())); //One X for each island tile in island group
            toPrint.append("): ");
            for(int islandTileID : islandGroups.get(islandGroupIdx)) {
                for (int studentID : islandTileStudentIDs.get(islandTileID)) {
                    students.add(archipelagoStudentColors.get(studentID));
                }
            }
            toPrint.append(studentFrequencyString(students));

            if(towerInfo.get(islandGroupIdx) != null) {
                toPrint.append(String.format(" (%d %s towers)", islandGroups.get(islandGroupIdx).size() , towerInfo.get(islandGroupIdx)));
            }
            if(game.getMotherNatureIslandGroupIdx() == islandGroupIdx) {
                toPrint.append(BRIGHT_RED + " (Mother Nature)" + RESET);
            }
            if(noEntryTiles.get(islandGroupIdx) > 0){
                toPrint.append(String.format(" (%d no entry tiles)", noEntryTiles.get(islandGroupIdx)));
            }

        }
        displayMessage(toPrint.toString());
    }

    public void displayClouds(){
        StringBuilder toPrint = new StringBuilder("CLOUDS:");
        List<Integer> clouds = game.getCloudIDs();
        for(int cloudID : clouds){
            toPrint.append(String.format("\nCloud %d: ", clouds.indexOf(cloudID) + 1));

            toPrint.append(studentFrequencyString(new ArrayList<>(game.getCloudStudentsIDs(cloudID).values())));
        }
        displayMessage(toPrint.toString());
    }

    public void displayCharactersShort(){
        StringBuilder toPrint = new StringBuilder("CHARACTERS:\n");
        for(int ID : game.getDrawnCharacterIDs()) {
            toPrint.append(characterStringShort(ID));
        }
        if(toPrint.lastIndexOf("\n") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("\n"));
        displayMessage(toPrint.toString());
    }

    public void displayCharactersDetailed(){
        StringBuilder toPrint = new StringBuilder("These are the CHARACTERS which have been picked for this game:\n\n");
        for(int ID : game.getDrawnCharacterIDs()) {
            toPrint.append(characterStringDetailed(ID));
        }
        if(toPrint.lastIndexOf("\n") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("\n"));
        displayMessage(toPrint.toString());
    }

    public void displayPlayerBoard(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(this.nickname)){ nickToWrite = "Your"; } else nickToWrite += "'s";
        displayMessage(nickToWrite + " board:");
        output.print("\033[1A"); //Move cursor up to not leave empty line
        displayEntrance(nickname);
        output.print("\033[1A"); //Move cursor up to not leave empty line
        displayDiningRoom(nickname);
        if(game.getGameMode() == GameMode.EXPERT) {
            output.print("\033[1A"); //Move cursor up to not leave empty line
            displayMessage("Coins owned: " + game.getCoins(nickname));
        }
    }

    public void displayEntrance(String nickname){
        displayMessage("ENTRANCE: " + studentFrequencyString(new ArrayList<>(game.getEntranceStudentsIDs(nickname).values())));
    }

    public void displayDiningRoom(String nickname){
        StringBuilder toPrint = new StringBuilder("TABLES");
        if(game.getGameMode() == GameMode.EXPERT)
            toPrint.append(" [Coins left]");
        toPrint.append(": ");

        for(Color c : Color.values()) {
            toPrint.append(String.format(colorMapping.get(c) + "%s: %d " + RESET, c, game.getTableStudentsIDs(nickname, c).size()));
            if(game.getProfessorsOwner().get(c) != null && game.getProfessorsOwner().get(c).equals(nickname))
                toPrint.append(colorMapping.get(c)).append("(Prof) ").append(RESET);
            if(game.getGameMode() == GameMode.EXPERT)
                toPrint.append(colorMapping.get(c)).append("[").append(game.getCoinsLeft(nickname, c)).append("] ").append(RESET);
        }

        displayMessage(toPrint.toString());
    }

    public void displayHand(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(this.nickname)){ nickToWrite = "your"; } else nickToWrite += "'s";
        StringBuilder toPrint = new StringBuilder("These are the ASSISTANTS still in " + nickToWrite + " hand:\n");
        for(int c : game.getCardsLeft(nickname)){
            toPrint.append(String.format("\nAssistant %d with move power %d", c, (c + 1)/2));
        }
        displayMessage(toPrint.toString());
    }

    public void displayPlayersInfo(){
        HashMap<String, TowerColor> towers = game.getPlayerTeams();
        HashMap<String, WizardType> wizards = game.getPlayerWizard();

        StringBuilder toPrint = new StringBuilder("Players: ");
        for(String nickname : game.getPlayers()){
            toPrint.append(nickname).append(" - ").
                    append(towers.get(nickname) == null ? "____" : towers.get(nickname)).append(", ").
                    append(wizards.get(nickname) == null ? "____" : wizards.get(nickname)).append(" | ");
        }
        if(toPrint.lastIndexOf("| ") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("| "));
        displayMessage(toPrint.toString());
    }

    public void displayCurrentOrder(){
        StringBuilder toPrint = new StringBuilder("This is the current " + game.getCurrentPhase() + " phase order:\n");
        int i = 1;
        for(String nickname : game.getPlayerOrder()){
            toPrint.append(i).append("-").append(nickname).append(" ");
            i++;
        }
        displayMessage(toPrint.toString());
    }

    public void displayPlayedCards(){
        StringBuilder toPrint = new StringBuilder("ASSISTANTS currently played [Move power]:\n");
        for(String nickname : game.getCardsPlayedThisRound().keySet()){
            toPrint.append(nickname).append(": ").append(game.getCardsPlayedThisRound().get(nickname)).
            append(" [").append((game.getCardsPlayedThisRound().get(nickname) + 1) /2).append("] | ");
        }
        if(toPrint.lastIndexOf("| ") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("| "));
        displayMessage(toPrint.toString());
    }

    public void displayAvailableCards(){
        for (String nickname : game.getPlayers()){
            displayHand(nickname);
        }
    }

    public void displayTowersLeft(){
        HashMap<TowerColor, Integer> towersLeft = game.getTowersLeft();
        StringBuilder toPrint = new StringBuilder();
        for(TowerColor towerColor : TowerColor.values()){
            if(towersLeft.get(towerColor) != null){
                toPrint.append("Team ").append(towerColor).append(" has ").append(towersLeft.get(towerColor));
                if (towersLeft.get(towerColor) == 1) {
                    toPrint.append(" tower left\n");
                } else {
                    toPrint.append(" towers left\n");
                }
            }
        }
        toPrint.deleteCharAt(toPrint.lastIndexOf("\n"));
        displayMessage(toPrint.toString());
    }

    public void displayAvailableCommands(){
        StringBuilder toPrint = new StringBuilder(
                "These are the available " + CYAN + "info " + RESET + "and " + RED + "game " + RESET + "commands right now:\n");
        for(Command command : infoCommandList){
            toPrint.append(CYAN).append(command.getCommandToWrite()).append(RESET).append(", ");
        }
        for(Command command : gameCommandList){
            toPrint.append(RED).append(command.getCommandToWrite()).append(RESET).append(", ");
        }
        toPrint.deleteCharAt(toPrint.lastIndexOf(", "));
        displayMessage(toPrint.toString());
    }

    private void printDivider(){
        output.println("--------------------------------------------------------------------------------------------");
    }

    private void clearScreen() {
        printDivider();
        output.println("\033[H\033[2J");
        //output.flush();
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
        if(colorFrequency.lastIndexOf(" ") != -1)
            colorFrequency.deleteCharAt(colorFrequency.lastIndexOf(" "));
        return colorFrequency.toString();
    }

    private int selectStudentFromContainer(RequestParameter type, int characterID){
        int studentID;
        Color colorSelection;
        HashMap<Integer, Color> studentIDs;

        switch (type){
            case STUDENT_ENTRANCE -> studentIDs = game.getEntranceStudentsIDs(nickname);
            case STUDENT_DINING_ROOM -> {
                studentIDs = new HashMap<>();
                for(Color color : Color.values()){
                    for(Integer studID : game.getTableStudentsIDs(nickname, color))
                        studentIDs.put(studID, color);
                }
            }
            case STUDENT_CARD -> studentIDs = game.getCharacterStudents(characterID);
            default -> throw new IllegalArgumentException("Given request parameter does not pertain to student choice"); //never here
        }

        colorSelection = Color.valueOf(parser.readLineFromSelection(studentIDs.values().stream()
                .map(Object::toString).collect(Collectors.toList())).toUpperCase());
        studentID = studentIDs.entrySet().stream().filter(o -> o.getValue() == colorSelection)
                .map(Map.Entry::getKey).findAny().orElse(-1);

        return studentID;
    }

    //Method lets select from 1 to size -> it will be displayed this way, not from 0 to size - 1. Then subtracts 1 to
    // have actual island group selected
    private int selectIslandTileFromIdx(){
        return game.getIslandTilesIDs().get(parser.readBoundNumber(1, game.getIslandTilesIDs().size()) - 1).get(0);
    }

    private Color selectColor(){
        //print colors?
        return Color.valueOf(parser.readLineFromSelection(Arrays.stream(Color.values())
                .map(Object::toString).collect(Collectors.toList())).toUpperCase());
    }

    private String characterStringShort(int ID){
        StringBuilder toPrint = new StringBuilder();
        if(ID < 10)
            toPrint.append(String.format("Character %d  [Cost: %d", ID, game.getCharacterCost(ID)));
        else
            toPrint.append(String.format("Character %d [Cost: %d", ID, game.getCharacterCost(ID)));
        List<Color> studentFrequencyList = game.getCharacterStudents(ID).values().stream().toList();
        if(studentFrequencyList.size() > 0){
            toPrint.append(" | Students: ");
            toPrint.append(studentFrequencyString(studentFrequencyList));
        }
        if(game.getNoEntryTilesCharacter(ID) > 0){
            toPrint.append(" | No entry Tiles: ");
            toPrint.append(game.getNoEntryTilesCharacter(ID));
        }
        if(game.getActiveCharacterID() != -1 && game.getActiveCharacterID() == ID){
            toPrint.append(BRIGHT_RED).append(" ACTIVE: ")
                    .append(game.getActiveCharacterUsesLeft()).append(" uses left").append(RESET);
        }
        toPrint.append("]\n");
        return toPrint.toString();
    }

    private String characterStringDetailed(int ID){
        StringBuilder toPrint = new StringBuilder();
        toPrint.append(String.format("Character %d\nCost:%d\nDescription: %s\n",
                ID, game.getCharacterCost(ID), CharactersDescription.values()[ID - 1].getDescription()));
        List<Color> studentFrequencyList = game.getCharacterStudents(ID).values().stream().toList();
        if(studentFrequencyList.size() > 0){
            toPrint.append("Students placed on this character: ");
            toPrint.append(studentFrequencyString(studentFrequencyList));
            toPrint.append("\n");
        }
        if(game.getNoEntryTilesCharacter(ID) > 0){
            toPrint.append(String.format("On this character there are %d no entry tiles left", game.getNoEntryTilesCharacter(ID)));
            toPrint.append("\n");
        }
        if(game.getActiveCharacterID() != -1 && game.getActiveCharacterID() == ID){
            toPrint.append(BRIGHT_RED).append("This character is ACTIVE and has ")
                    .append(game.getActiveCharacterUsesLeft()).append(" uses left").append(RESET);
            toPrint.append("\n");
        }
        toPrint.append("\n");
        return toPrint.toString();
    }

    private void mapColors(){
        colorMapping = new HashMap<>();
        colorMapping.put(Color.RED, RED);
        colorMapping.put(Color.PINK, MAGENTA);
        colorMapping.put(Color.YELLOW, YELLOW);
        colorMapping.put(Color.GREEN, GREEN);
        colorMapping.put(Color.BLUE, CYAN);
    }
}
