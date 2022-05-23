package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Info.PingInfo;
import it.polimi.ingsw.Network.Message.Info.ServerLoginInfo;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.Update.Update;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.View.CLI.CLI;
import it.polimi.ingsw.View.GUI.GUI;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {

    private final String defaultServerIP;
    private String serverIP;
    private final int defaultServerPort;
    private int serverPort;

    private UI UI;

    private String nickname;

    /**
     * The last update receive. It is used to ask again the UI for input when error is received.
     */
    private Update lastUpdate;

    private Socket socket;
    private OutputStream out;
    private ObjectOutputStream outObj;
    private InputStream in;
    private ObjectInputStream inObj;

    /**
     * Thread queue where all operations that must request something to the user are executed.
     */
    private final ExecutorService requestQueue;
    /**
     * Thread queue where all operations that must only show information to the user are executed.
     */
    private final ExecutorService infoQueue;
    private Ping ping;
    /**
     * Executor which will only execute ping related threads.
     */
    private ExecutorService pingExecutor;

    private boolean gameStarted;

    /**
     * Constructor for the Client
     * @param chosenUI represents a CLI or a GUI.
     */
    public Client(String chosenUI){
        if(chosenUI != null){
            if(chosenUI.equals("cli"))
                UI = new CLI(this);
            if (chosenUI.equals("gui"))
                UI = new GUI(this);
        }
        defaultServerIP = "127.0.0.1";
        defaultServerPort = 4646;
        requestQueue = Executors.newSingleThreadExecutor();
        infoQueue = Executors.newSingleThreadExecutor();
        gameStarted = false;
    }

    /**
     * Main method that starts by requesting whether the user wants to use a CLI or a GUI.
     * Then, server IP and port are requested though the chosen UI, the connection is established
     * and the ping is started. A nickname is then requested and finally the main loop of receiving server responses
     * is started.
     */
    public void start(){
        if(UI == null) //Skipped if --cli or --gui option
            askCLIorGUI();

        while (socket == null){ //Asks for server info
                Map<String, String> serverInfo = UI.requestServerInfo(defaultServerIP, defaultServerPort);
                serverIP = serverInfo.get("IP");
                serverPort = Integer.parseInt(serverInfo.get("port"));
            connectToLobbyServer(serverIP, serverPort);
        }

        startPing(); //Starts heartbeat with lobby server on another thread
        requestQueue.execute(() -> {
            nickname = UI.requestNickname();
            UI.setNickname(nickname);
            tryLobbyLogin();
        });

        while (true){
            parseMessage( receiveMessage() );
        }
    }

    //region Server connection

    /**
     * Tries to connect to the lobby server at the given address.
     * @param IP ip to connect to
     * @param port port to connect to
     */
    private void connectToLobbyServer(String IP, int port){
        try{
            socket = new Socket(IP, port);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            UI.displayInfo("Connected to lobby server.");
            socket.setSoTimeout(5000);
        } catch (IOException e) {
            UI.displayError("Could not connect to server: " + e.getLocalizedMessage(), false);
        }
    }

    /**
     * Tries to login by sending a login user action with chosen username.
     */
    private void tryLobbyLogin(){
        LoginUserAction login;
        login = new LoginUserAction(nickname);
        sendUserAction(login);
    }

    /**
     * Disconnects user from server.
     */
    private void disconnectFromLobby(){
        try {
            socket.close();
        } catch (IOException e) {
            UI.displayError("Error in disconnecting from server: " + e.getLocalizedMessage(), true);
        }
    }

    /**
     * Tries to connect to the match server at the given address.
     * @param IP ip to connect to
     * @param port port to connect to
     */
    private void connectToMatchServer(String IP, int port){
        try{
            socket = new Socket(IP, port);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            socket.setSoTimeout(2000);
            UI.displayInfo("Connected to match server.");
        } catch (IOException e) {
            UI.displayError("Could not connect to match server: " + e, true);
            reset();
        }
        sendUserAction(new LoginUserAction(nickname));
    }

    /**
     * Starts ping with the server to which the socket is connected.
     */
    private void startPing(){
        ping = new Ping(this);
        pingExecutor = Executors.newSingleThreadExecutor();
        pingExecutor.execute(ping);
    }

    /**
     * Stops ping with the server to which the socket is connected. Waits for the ping to have stopped before returning.
     */
    private void stopPing(){
        pingExecutor.shutdownNow();
        boolean error;
        try { //Ensures ping was successfully stopped, to avoid error when socket is closed
            error = ! pingExecutor.awaitTermination(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            error = true;
        }
        if (error){
            UI.displayError("Error occurred while shutting down ping." , true);
            reset();
        }
    }
    //endregion

    /**
     * Asks on the terminal whether the user wants to play with CLI or GUI
     */
    private void askCLIorGUI(){
        Scanner sysIn = new Scanner(System.in);
        int viewChoice = 0;
        while (viewChoice != 1 && viewChoice != 2){
            System.out.println("Select which interface you would prefer to play with:\n1.CLI\n2.GUI");
            String s = sysIn.nextLine();
            try{
                viewChoice = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored){}
            clearScreen();
            switch (viewChoice){
                case 1 -> {
                    UI = new CLI(this);
                    System.out.println("CLI selected!");
                }
                case 2 -> {
                    UI = new GUI(this);
                    System.out.println("GUI selected!");
                }
                default -> {
                    System.out.println("Invalid choice! Please, select again.\n");
                }
            }
        }
    }

    //region Parsing

    /**
     * Parses a received message.
     * @param message a message sent by the server.
     */
    private void parseMessage(Message message){
        if(message instanceof PingInfo){
            ping.received();
        }
        else if(message instanceof LoginError){
            requestQueue.execute(() -> {
                UI.displayError(message.toString(), false);
                nickname = UI.requestNickname();
                UI.setNickname(nickname);
                tryLobbyLogin();
            });
        }
        else if(message instanceof ServerLoginInfo){
            UI.displayInfo("Connecting to port: " + ((ServerLoginInfo) message).getPort());
            stopPing(); //Stops heartbeat with lobby server to avoid writing on the socket that is subsequently closed
            disconnectFromLobby();
            connectToMatchServer(serverIP, ((ServerLoginInfo) message).getPort());
            startPing(); //Starts heartbeat with match server
        }
        else if (message instanceof Update){
            parseUpdate((Update) message);
        }
        else if (message instanceof Error){
            UI.displayError(message.toString(), false);
            parseUpdate(lastUpdate);
        }
        else{
            UI.displayError("Server didn't send correct information.", true);
            reset();
        }
    }

    /**
     * If the server sent an update, this method parses the update based on the action taking player.
     * If the action taking player of the update is this client, then executes in a thread queue.
     * If it isn't, it executes on a different thread queue.
     * This allows the Client to make request of the user and show them info in parallel.
     * @param update the update received.
     */
    private void parseUpdate(Update update){
        if(nickname.equals(update.getActionTakingPlayer()) && update.getNextUserAction() != null){
            lastUpdate = update;
            requestQueue.execute(() ->{
                requestAction(update);
                UI.notifyServerResponse();
            });
        }
        else {
            infoQueue.execute(() -> {
                displayInfo(update);
                UI.notifyServerResponse();
            });
        }
    }

    /**
     * Parses the update on the premise of making the user a request of some kind, either by directly
     * requesting it or by enabling and disabling commands that allow the user to take an action.
     * @param update the update received.
     */
    private void requestAction(Update update){
        if(update.getPlayerActionTaken() != null && update.getUserActionTaken()!= null)
            UI.displayInfo(update.getPlayerActionTaken() + " " + update.getUserActionTaken().getActionTakenDesc());

        List<Command> toDisable = new ArrayList<>();
        List<Command> toEnable = new ArrayList<>();
        switch (update.getNextUserAction()){
            //During setup, action are requested in a different thread to also allow info on others' actions to be received
            case GAME_SETTINGS -> {
                UI.requestGameSettings();
            }
            case TOWER_COLOR -> {
                UI.requestTowerColor(update.getGame());
            }
            case WIZARD -> {
                UI.requestWizard(update.getGame());
            }
            //No input is accepted when here, client is waiting for others to complete their selection (and receive PLAY_ASSISTANT)
            //Commands are enabled but main thread isn't started yet. Commands are "pre-loaded".
            case WAIT_GAME_START -> {
                UI.displayInfo("Please wait for all players to be ready and for your turn to start...");
            }
            //During the game, a main thread is started that puts CLI/GUI in cycle waiting for an action to be chosen.
            //The possible actions are enabled and disabled by the client.
            case PLAY_ASSISTANT -> {
                toDisable.addAll(Arrays.asList(Command.CLOUD, Command.CHARACTER));
                toEnable.add(Command.ASSISTANT);
                if(!gameStarted){
                    gameStarted = true;
                    toEnable.addAll(Arrays.asList(Command.QUIT, Command.HELP, Command.CARDS, Command.TABLE, Command.ORDER));
                    if(update.getGame().getGameMode() == GameMode.EXPERT)
                        toEnable.add(Command.CHARACTER_INFO);
                    UI.startGame();
                }
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case MOVE_STUDENT -> {
                toDisable.add(Command.ASSISTANT);
                if(update.getGame().getGameMode() == GameMode.EXPERT && update.getGame().getActiveCharacterID() == -1)
                    toEnable.add(Command.CHARACTER);
                toEnable.add(Command.MOVE);
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case MOVE_MOTHER_NATURE -> {
                toDisable.add(Command.MOVE);
                toEnable.add(Command.MOTHER_NATURE);
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case TAKE_FROM_CLOUD -> {
                toDisable.add(Command.MOTHER_NATURE);
                toEnable.add(Command.CLOUD);
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case USE_ABILITY -> {
                if(update.getGame().getActiveCharacterUsesLeft() > 0) {
                    toDisable.add(Command.CHARACTER);
                    toEnable.add(Command.ABILITY);
                }
                else {
                    toDisable.add(Command.CHARACTER);
                    toDisable.add(Command.ABILITY);
                }
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
        }


        if(update.getActionTakingPlayer() != null && update.getNextUserAction()!= null)
            UI.displayInfo(update.getActionTakingPlayer() + " " + update.getNextUserAction().getActionToTake());
    }

    /**
     * Parses the update on the premise of showing the player some information.
     * Nothing will be requested of the user, at most some commands will be disabled based on what action is
     * expected to be taken.
     * @param update the update received.
     */
    private void displayInfo(Update update){
        if(update.getPlayerActionTaken() != null && update.getUserActionTaken()!= null)
            UI.displayInfo(update.getPlayerActionTaken() + " " + update.getUserActionTaken().getActionTakenDesc());
        //Info will be a different colored text in CLI or a pop-up in GUI, parallel to asking for action.
        //Allows information about other players action while user is selecting (useful for parallel login)
        List<Command> toDisable = new ArrayList<>();
        List<Command> toEnable = new ArrayList<>();
        switch (update.getNextUserAction()){
            case TOWER_COLOR, WAIT_GAME_START, WIZARD -> {
            }
            case PLAY_ASSISTANT -> {
                toDisable.addAll(Arrays.asList(Command.ASSISTANT, Command.CLOUD, Command.CHARACTER, Command.ABILITY));
                if(!gameStarted){
                    gameStarted = true;
                    toEnable.addAll(Arrays.asList(Command.QUIT, Command.HELP, Command.CARDS, Command.TABLE, Command.ORDER));
                    if(update.getGame().getGameMode() == GameMode.EXPERT)
                        toEnable.add(Command.CHARACTER_INFO);
                    UI.startGame();
                }
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);

            }
            case MOVE_STUDENT -> {
                toDisable.addAll(Arrays.asList(Command.ASSISTANT, Command.CLOUD, Command.CHARACTER, Command.ABILITY));
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case MOVE_MOTHER_NATURE, USE_ABILITY, TAKE_FROM_CLOUD -> {
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case END_GAME -> {
                for(Command command : Command.values()){
                    if(command != Command.QUIT)
                        toDisable.add(command);
                }
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
                UI.displayInfo("Team " + update.getGame().getWinner() + " has WON!!!");
            }
        }

        if(update.getActionTakingPlayer() != null && update.getNextUserAction()!= null)
            UI.displayInfo(update.getActionTakingPlayer() + " " + update.getNextUserAction().getActionToTake());
    }

    //endregion

    //region Network send/receive

    /**
     * Sends a UserAction through the socket.
     * @param userAction the UserAction to be sen.
     */
    public synchronized void sendUserAction(UserAction userAction){
        try {
            outObj.writeObject(userAction);
            outObj.flush();
            outObj.reset();

        } catch (IOException e) {
            UI.displayError("Unable to write to server: " + e.getLocalizedMessage(), true);
            reset();
        }
    }

    /**
     * Receives a message from the server through the socket.
     * @return
     */
    private Message receiveMessage(){
        Object message = null;
        try {
            message = inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            UI.displayError("Unable to receive messages from server: " + e.getLocalizedMessage(), true);
            reset();
        }
        if(!(message instanceof Message)){
            UI.displayError("Server didn't send correct information.", true);
            reset();
        }
        return (Message) message;
    }
    //endregion

    /**
     * Clears the terminal.
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Closes the application.
     */
    private void reset(){
        try {
            socket.close();
        } catch (IOException e) {
            System.exit(-1);
        }
        System.exit(-1); //FIXME: actual reset

    }
}
