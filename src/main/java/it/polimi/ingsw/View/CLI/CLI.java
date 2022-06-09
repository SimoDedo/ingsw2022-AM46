package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.Exceptions.HelpException;
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

    private final ExecutorService operations;

    public CLI(Client client) {
        lock = new Object();
        operations = Executors.newSingleThreadExecutor();
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

    /**
     * @param nickname of the player using this interface
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void update(ObservableByClient game) {
        this.game = game;
    }

    @Override
    public void startGame(){
        gameStarted = true;
        operations.execute(() -> {
            boolean quit = false;
            while (!quit){
                String commandString = "";
                do {
                    try { commandString = parser.readLine();
                    } catch (HelpException e) { displayHelp(); }
                } while(commandString.equals(""));
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
                        standings();
                        displayAvailableCommands();
                    }
                }
                else{
                    displayInvalid();
                    standings();
                    displayAvailableCommands();
                }
            }
            System.exit(0);
        });
    }

    private void waitForServerResponse(){
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
    public void notifyServerResponse(){
        if(this.gameStarted){
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
        displayWelcome();
        info.put("port", String.valueOf( requestServerPort(defaultPort)));
        return info;
    }

    private int requestServerPort(int defaultPort){
        int port = -1;
        // hmm....
        String portString = "loop";
        while (port<0 || port>65535){
            displayMessage(String.format("Choose server port [press enter for %d]:", defaultPort));
            do {
                try { portString = parser.readLine();
                } catch (HelpException e) { displayHelp("port"); }
            } while(portString.equals("loop"));
            try{
                port = Integer.parseInt(portString);
            }
            catch (NumberFormatException ignored){}
            if(portString.equals(""))
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
        String IPString = "loop";
        do {
            try { IPString = parser.readLine();
            } catch (HelpException e) { displayHelp("IP"); }
        } while(IPString.equals("loop"));

        if(IPString.equals(""))
            IPString = defaultIP;
        clearScreen();
        return IPString;
    }

    /**
     * Asks for the user to choose a nickname, which is then sent to the server
     * @return the nickname to be saved here and in the Client class, after server confirmation
     */
    @Override
    public String requestNickname() {
        displayWelcome();
        displayErrorQueue();
        displayMessage("Choose a nickname:");

        String nickname = "";

        do {
            try { nickname = parser.readLine();
                clearScreen();
                if(nickname.equals("") || !Character.isLetterOrDigit(nickname.charAt(0)) || nickname.length() > 50){
                    displayMessage("Nickname should start with a letter or number and should be less than 50 characters " +
                            "long! Choose another nickname:");
                }
            } catch (HelpException e) { displayHelp("nick"); }
        } while(nickname.equals("") || !Character.isLetterOrDigit(nickname.charAt(0)) || nickname.length() > 50);

        return nickname;
    }

    /**
     * Groups GameMode and player number requests into GameSettingsUserAction
     */
    @Override
    public void requestGameSettings(){
        displayErrorQueue();
        operations.execute(() ->{
            GameMode gameMode = requestGameMode();
            clearScreen();
            int numOfPlayers = requestPlayerNumber();
            clearScreen();
            UserAction gameSettingsRequest = new GameSettingsUserAction(nickname, numOfPlayers, gameMode);

            client.sendUserAction(gameSettingsRequest);
        });
    }

    /**
     * Asks for the (first) user to choose if the game that will be created should be of Game or GameExpert type
     * @return the chosen game mode
     */
    private GameMode requestGameMode(){
        displayMessage("Select a game mode:\n1 Normal\n2 Expert");
        int gameModeSelection = -1;
        do {
            try { gameModeSelection = parser.readBoundNumber(1, 2);
            } catch (HelpException e) { displayHelp("game-mode"); }
        } while(gameModeSelection == -1);

        return GameMode.values()[gameModeSelection - 1];
    }

    /**
     * Asks for the (first) user to choose how many players will be able to connect to this match (2-4)
     * @return the chosen number of players
     */
    private int requestPlayerNumber(){
        displayMessage("Select how many players will participate in the game [2,3 or 4]:");
        int playerNumberSelection = -1;
        do {
            try { playerNumberSelection = parser.readBoundNumber(2, 4);
            } catch (HelpException e) { displayHelp("player-num"); }
        } while(playerNumberSelection == -1);
        return playerNumberSelection ;
    }

    /**
     * Asks for the user to pick a tower color from the available ones
     */
    @Override
    public void requestTowerColor(ObservableByClient game){
        update(game);
        displayErrorQueue();
        operations.execute(() ->{
            List<TowerColor> available = this.game.getAvailableTowerColors();
            int sel = 1;
            int towerColorSelection = -1;

            displayPlayersInfo();
            StringBuilder toPrint = new StringBuilder("Please choose a tower color:\n");
            for (TowerColor towerColor : available){
                toPrint.append(sel).append(": ").append(towerColor).append("\n");
                sel++;
            }
            displayMessage(toPrint.toString());

            do {
                try { towerColorSelection = parser.readBoundNumber(1, available.size());
                } catch (HelpException e) { displayHelp("tower-color"); }
            } while(towerColorSelection == -1);

            TowerColor selection = available.get(towerColorSelection - 1);

            clearScreen();
            UserAction towerColorRequest = new TowerColorUserAction(nickname, selection);
            client.sendUserAction(towerColorRequest);
        });
    }

    /**
     * Asks the user to choose a WizardType from a list of available ones
     */
    @Override
    public void requestWizard(ObservableByClient game){
        update(game);
        displayErrorQueue();
        operations.execute(() -> {
            List<WizardType> available = this.game.getAvailableWizards();
            int sel = 1;
            int wizardTypeSelection = -1;

            displayPlayersInfo();
            StringBuilder toPrint = new StringBuilder("Please choose your wizard:\n");
            for (WizardType wizardType : available){
                toPrint.append(sel).append(": ").append(wizardType).append("\n");
                sel++;
            }
            displayMessage(toPrint.toString());

            do {
                try { wizardTypeSelection = parser.readBoundNumber(1, available.size());
                } catch (HelpException e) { displayHelp("wizard"); }
            } while(wizardTypeSelection == -1);
            WizardType selection = available.get(wizardTypeSelection - 1);

            clearScreen();
            UserAction wizardRequest = new WizardUserAction(nickname, selection);
            client.sendUserAction(wizardRequest);
        });
    }

    @Override
    public void requestWaitStart() {
        displayInfo("Please wait for all players to be ready and for your turn to start...");

    }
    /**
     * Asks user to pick an assistant to play from the ones he is holding
     */
    private void requestAssistant(){
        displayHand(this.nickname);
        displayPlayedCards();
        displayMessage("Type the number of the assistant you would like to play.");

        int assistantIDSelection = -1;

        do {
            try { assistantIDSelection = parser.readNumberFromSelection(game.getCardsLeft(nickname));;
            } catch (HelpException e) { displayHelp("assistant"); }
        } while(assistantIDSelection == -1);

        UserAction assistantRequest = new PlayAssistantUserAction(nickname, assistantIDSelection);

        client.sendUserAction(assistantRequest);
    }

    /**
     * Asks for the user to select a student from his entrance, then asks where to move it.
     */
    private void requestMoveFromEntrance(){
        HashMap<Integer, Color> studentIDs = game.getEntranceStudentsIDs(nickname);

        displayEntrance(nickname);
        displayMessage("Select a student from your entrance:");
        int studentID = selectStudentFromContainer(RequestParameter.STUDENT_ENTRANCE, -1);
        int destinationID;
        int destinationType = -1;

        displayMessage("Select destination type:\n1: Islands\n2: Dining Room");

        do {
            try { destinationType = parser.readBoundNumber(1, 2);
            } catch (HelpException e) { displayHelp("entrance-dst"); }
        } while(destinationType == -1);

        if(destinationType == 1){
            displayArchipelago();
            displayMessage("Select the island group number you would like to place your student in:");
            destinationID = selectIslandTileFromIdx();
        } else {
            destinationID = game.getTableIDs(nickname).get(studentIDs.get(studentID));
        }

        UserAction moveStudentRequest = new MoveStudentUserAction(nickname, studentID, destinationID);

        client.sendUserAction(moveStudentRequest);
    }

    /**
     * Asks user to decide how many steps mother nature will take. Sends destination island ID to client.
     */
    private void requestMotherNature(){
        HashMap<Integer, List<Integer>> islandGroups = game.getIslandTilesIDs();
        int movePower = game.getActualMovePower(nickname);
        int steps = -1;

        displayArchipelago();
        displayMessage(String.format("Type the number of spaces mother nature should move (max %d):", movePower));

        do {
            try { steps = parser.readBoundNumber(1, movePower);
            } catch (HelpException e) { displayHelp("mother-nature"); }
        } while(steps == -1);

        int dstIslandGroup = game.getMotherNatureIslandGroupIdx() + steps;
        dstIslandGroup = dstIslandGroup < islandGroups.size() ?
                dstIslandGroup : dstIslandGroup - islandGroups.size();
        int dstIslandID = islandGroups.get(dstIslandGroup).get(0);

        UserAction moveMotherNatureRequest = new MoveMotherNatureUserAction(nickname, dstIslandID);

        client.sendUserAction(moveMotherNatureRequest);
    }

    /**
     * Asks user to pick a cloud, then translates the user selection to a cloud ID using the cloudMap
     */
    private void requestCloud(){
        List<Integer> clouds = game.getCloudIDs();
        displayClouds();
        displayMessage("Type the number of the cloud you would like to choose.");
        int cloudIdxSelection = -1;

        do {
            try { cloudIdxSelection = parser.readBoundNumber(1, game.getCloudIDs().size());
            } catch (HelpException e) { displayHelp("cloud"); }
        } while(cloudIdxSelection == -1);

        int cloudIDSelection = clouds.get(cloudIdxSelection - 1);
        UserAction cloudRequest = new TakeFromCloudUserAction(nickname, cloudIDSelection);

        client.sendUserAction(cloudRequest);
    }

    /**
     * Asks for the user to purchase a character by typing its ID
     */
    private void requestCharacter(){
        displayCharactersDetailed();
        displayMessage("Select the number of the character you want to activate!");

        int characterID = -1;
        do {
            try { characterID = parser.readNumberFromSelection(game.getDrawnCharacterIDs());
            } catch (HelpException e) { displayHelp("character"); }
        } while(characterID == -1);

        UserAction characterRequest = new UseCharacterUserAction(nickname, characterID);

        client.sendUserAction(characterRequest);
    }

    /**
     * Goes through the current request parameters and asks for the user to fill in all the information necessary to
     * use the character's ability
     */
    private void requestCharacterAbility(){
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
                    displayArchipelago();
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

    /**
     * TODO:Gives the option to purchase and activate a character right before ending the turn
     */
    private void requestEndTurn(){
        displayMessage("Would you to play a character before ending your turn? y/n");


        String endTurnSelection = "";
        do {
            try { endTurnSelection = parser.readLineFromSelection(new ArrayList<>(Arrays.asList("y", "n")));
            } catch (HelpException e) { displayHelp("end-turn"); }
        } while(endTurnSelection.equals(""));

        if(endTurnSelection.equals("n")){
            UserAction endTurnRequest = new EndTurnUserAction(nickname);
            client.sendUserAction(endTurnRequest);
        }
    }

    private void displayMessage(String message) {
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
    public void displayError(String error, boolean isFatal) {
        if(isFatal){
            System.out.println(RED + error + RESET);
            endGame();
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
    public void updateSetup(ObservableByClient game, UserActionType actionTaken) {
        update(game);
        if(actionTaken == UserActionType.TOWER_COLOR){ //TODO: just a draft on how it could work CLI side.
            StringBuilder toPrint = new StringBuilder("Tower colors remaining:\n");
            for (TowerColor towerColor : game.getAvailableTowerColors()){
                toPrint.append(towerColor).append("\n");
            }
            displayMessage(toPrint.toString());
        }
        else {
            StringBuilder toPrint = new StringBuilder("Wizards remaining:\n");
            for (WizardType wizardType : game.getAvailableWizards()){
                toPrint.append(wizardType).append("\n");
            }
            displayMessage(toPrint.toString());
        }
    }

    @Override
    public void displayBoard(ObservableByClient game, UserActionType actionTaken) {
        update(game);
    }

    @Override
    public void displayWinners(TowerColor winner, List<String> winners) {
        StringBuilder toPrint = new StringBuilder();
        if(winners.contains(nickname)){
            toPrint.append("CONGRATULATIONS ");
            for(String player : winners){
                toPrint.append(player).append(" ");
            }
            toPrint.append("!! Team ").append(winner).append(" has WON!!!");
        }
        else {
            toPrint.append("Too bad! ");
            for(String player : winners){
                toPrint.append(player).append(" ");
            }
            toPrint.append("you lost! Team ").append(winner).append(" has won.");
        }
        this.displayInfo(toPrint.toString());
        notifyServerResponse();
        endGame();
    }

    private void endGame() {
        System.exit(0);
    }

    private void displayWelcome() {
        clearScreen();
        output.println(
                "\u001B[107;40m\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;m/\u001B[38;5;m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;160m/\u001B[38;5;m/\u001B[38;5;m(\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;160m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m%\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;160m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;196m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;160m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;196m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m%\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m \u001B[38;5;016m \u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;196m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;m*\u001B[38;5;m/\u001B[38;5;160m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;009m/\u001B[38;5;m/\u001B[38;5;m/\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m ");
        output.println(
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;214m(\u001B[38;5;m(\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;036m/\u001B[38;5;m/\u001B[38;5;m/\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m%\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m(\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m,\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m.\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;m%\u001B[38;5;m \u001B[38;5;m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;m \u001B[38;5;m%\u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m%\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m%\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m%\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;m/\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;m/\u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m*\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m/\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m.\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m \u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m,\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m(\u001B[38;5;m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;169m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;m*\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;039m#\u001B[38;5;m#\u001B[38;5;m#\u001B[38;5;m(\u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \u001B[38;5;016m \n" +
                        "\u001B[0m");
        displayMessage("                                                    WELCOME TO ERIANTYS!\n");
    }

    private void displayHelp(){
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

    /**
     * Whenever user input is requested, if the user types "help" instead of one of the expected inputs a help
     * message is printed based on the context
     * @param context where the help function has been called from
     */
    private void displayHelp(String context){
        switch (context){
            case "port" -> displayMessage("Input the port of the server you would like to connect to. Press enter for" +
                    "the default port");
            case "IP" -> displayMessage("Input the IP of the server you would like to connect to. Press enter for the" +
                    "default IP");
            case "nick" -> displayMessage("Input an unique nickname (everything is allowed except for \"help\").");
            case "student-color" -> displayMessage("Type the name of the color of the student you would like to pick " +
                    "from the available ones.");
            case "color" -> displayMessage("Type the name of any color you would like to select.");
            case "island" -> displayMessage("Type the number of the island group you would like to select.");
            case "tower-color" -> displayMessage("Type the name of the tower color you would like to pick.");
            case "game-mode" -> displayMessage("Type 1 to play without characters or 2 to play with characters enabled.");
            case "player-num" -> displayMessage("Type the number of players that will participate in this match.");
            case "wizard" -> displayMessage("Type the name of the wizard faction you would like to pick.");
            case "assistant" -> displayMessage("Choose an assistant to play by typing it's ID (the ID is equal to the " +
                    "turn order number).");
            case "cloud" -> displayMessage("Type the number of the cloud whose students you would like to add " +
                    "to your entrance.");
            case "mother-nature" -> displayMessage("Decide how much the mother nature pawn will advance by typing the " +
                    "number of steps, limited by the assistant card you played this turn.\n " +
                    "If you have purchased character 4 this turn you may choose to move two extra steps.");
            case "entrance-dst" -> displayMessage("Choose a destination type between islands or dining room by " +
                    "typing 1 for the former and 2 for the latter.");
            case "character" -> displayMessage("Type the number corresponding to the ID of the character you " +
                    "would like to purchase.");
            //TODO: character can be played at the end of turn??
            case "end-turn" -> displayMessage("Type \"y\" if you would like to end your turn, or \"n\" if you " +
                    "would like to purchase a character or use a character ability.");
        }
    }

    private void displayInvalid(){
        displayMessage("Unrecognized command - please type help for a list of available commands.");
    }

    /**
     * Displays all the information on the ongoing match
     */
    private void standings(){
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

    private void displayTurnInfo(){
        String toPrint = "Phase: " + game.getCurrentPhase() +
                " | Turn of: " +
                game.getCurrentPlayer();
        displayMessage(toPrint);
    }

    private void displayArchipelago(){
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

    private void displayClouds(){
        StringBuilder toPrint = new StringBuilder("CLOUDS:");
        List<Integer> clouds = game.getCloudIDs();
        for(int cloudID : clouds){
            toPrint.append(String.format("\nCloud %d: ", clouds.indexOf(cloudID) + 1));

            toPrint.append(studentFrequencyString(new ArrayList<>(game.getCloudStudentsIDs(cloudID).values())));
        }
        displayMessage(toPrint.toString());
    }

    private void displayCharactersShort(){
        StringBuilder toPrint = new StringBuilder("CHARACTERS:\n");
        for(int ID : game.getDrawnCharacterIDs()) {
            toPrint.append(characterStringShort(ID));
        }
        if(toPrint.lastIndexOf("\n") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("\n"));
        displayMessage(toPrint.toString());
    }

    private void displayCharactersDetailed(){
        StringBuilder toPrint = new StringBuilder("These are the CHARACTERS which have been picked for this game:\n\n");
        for(int ID : game.getDrawnCharacterIDs()) {
            toPrint.append(characterStringDetailed(ID));
        }
        if(toPrint.lastIndexOf("\n") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("\n"));
        displayMessage(toPrint.toString());
    }

    private void displayPlayerBoard(String nickname){
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

    private void displayEntrance(String nickname){
        displayMessage("ENTRANCE: " + studentFrequencyString(new ArrayList<>(game.getEntranceStudentsIDs(nickname).values())));
    }

    private void displayDiningRoom(String nickname){
        StringBuilder toPrint = new StringBuilder("TABLES");
        if(game.getGameMode() == GameMode.EXPERT)
            toPrint.append(" [Coins left]");
        toPrint.append(": ");

        for(Color c : Color.values()) {
            toPrint.append(String.format(colorMapping.get(c) + "%s: %d " + RESET, c, game.getTableStudentsIDs(nickname, c).size()));
            if(game.getProfessorsOwner().get(c) != null && game.getProfessorsOwner().get(c).equals(nickname))
                toPrint.append(colorMapping.get(c)).append("(Prof) ").append(RESET);
            if(game.getGameMode() == GameMode.EXPERT)
                toPrint.append(colorMapping.get(c)).append("[").append(game.getTableCoinsLeft(nickname, c)).append("] ").append(RESET);
        }

        displayMessage(toPrint.toString());
    }

    private void displayHand(String nickname){
        String nickToWrite = nickname;
        if(nickname.equals(this.nickname)){ nickToWrite = "your"; } else nickToWrite += "'s";
        StringBuilder toPrint = new StringBuilder("These are the ASSISTANTS still in " + nickToWrite + " hand:\n");
        for(int c : game.getCardsLeft(nickname)){
            toPrint.append(String.format("\nAssistant %d with move power %d", c, (c + 1)/2));
        }
        displayMessage(toPrint.toString());
    }

    private void displayPlayersInfo(){
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

    private void displayCurrentOrder(){
        StringBuilder toPrint = new StringBuilder("This is the current " + game.getCurrentPhase() + " phase order:\n");
        int i = 1;
        for(String nickname : game.getPlayerOrder()){
            toPrint.append(i).append("-").append(nickname).append(" ");
            i++;
        }
        displayMessage(toPrint.toString());
    }

    private void displayPlayedCards(){
        StringBuilder toPrint = new StringBuilder("ASSISTANTS currently played [Move power]:\n");
        for(String nickname : game.getCardsPlayedThisRound().keySet()){
            toPrint.append(nickname).append(": ").append(game.getCardsPlayedThisRound().get(nickname)).
            append(" [").append((game.getCardsPlayedThisRound().get(nickname) + 1) /2).append("] | ");
        }
        if(toPrint.lastIndexOf("| ") != -1)
            toPrint.deleteCharAt(toPrint.lastIndexOf("| "));
        displayMessage(toPrint.toString());
    }

    private void displayAvailableCards(){
        for (String nickname : game.getPlayers()){
            displayHand(nickname);
        }
    }

    private void displayTowersLeft(){
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


    /**
     * displays commands that are currently being accepted by the server
     */
    private void displayAvailableCommands(){
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

    /**
     * @param students each student's color
     * @return a string displaying how many students there are for each color
     */
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

    /**
     * Returns the ID of a student chosen from the container specified by the parameters
     * @param type of student container the student is being selected from, can be entrance, dining room or empty if
     *             the container in question is a character.
     * @param characterID the ID of the character from which the student is being selected. -1 if the container is
     *                   not a character
     * @return the ID of the selected student
     */
    private int selectStudentFromContainer(RequestParameter type, int characterID){
        int studentID;
        String colorSelectionString = "";
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

        do{
            try{ colorSelectionString = parser.readLineFromSelection(studentIDs.values().stream()
                    .map(Object::toString).collect(Collectors.toList()));
            } catch (HelpException e) { displayHelp("student-color"); }
        } while (colorSelectionString.equals(""));

        colorSelection = Color.valueOf(colorSelectionString.toUpperCase());
        studentID = studentIDs.entrySet().stream().filter(o -> o.getValue() == colorSelection)
                .map(Map.Entry::getKey).findAny().orElse(-1);

        return studentID;
    }

    /**
     * Asks for the user to select an island group by typing its index, then returns the first tile's ID in that group
     * @return the ID of an island tile contained by the selected island group
     */
    //Method lets select from 1 to size -> it will be displayed this way, not from 0 to size - 1. Then subtracts 1 to
    // have actual island group selected
    private int selectIslandTileFromIdx(){
        int groupIdxSelection = -1;
        do{
            try{ groupIdxSelection = parser.readBoundNumber(1, game.getIslandTilesIDs().size());
            } catch (HelpException e){ displayHelp("island"); }
        } while (groupIdxSelection == -1);

        return game.getIslandTilesIDs().get(groupIdxSelection - 1).get(0);
    }

    /**
     * Asks for the user to select a student color from all the possible values
     * @return selected color
     */
    private Color selectColor(){
        String colorSelection = "";

        do{
            //lambda maps the contents of the array of available colors to String to check against user input
            try{ colorSelection = parser.readLineFromSelection(Arrays.stream(Color.values())
                    .map(Object::toString).collect(Collectors.toList()));
            } catch (HelpException e) { displayHelp("color");}
        } while (colorSelection.equals(""));

        return Color.valueOf(colorSelection.toUpperCase());
    }


    /**
     * @param ID of the character to display the info of
     * @return a string with the character ID and cost and, if applicable, the students, no entry tiles and uses left.
     */
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
            toPrint.append(" | ").append(BRIGHT_RED).append("ACTIVE: ")
                    .append(game.getActiveCharacterUsesLeft()).append(" uses left").append(RESET);
        }
        toPrint.append("]\n");
        return toPrint.toString();
    }

    /**
     * @param ID of the character to display the info of
     * @return a string with the character ID and cost and, if applicable, the students, no entry tiles and uses left.
     */
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
