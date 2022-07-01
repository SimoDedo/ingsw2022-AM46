package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.Error.DisconnectionError;
import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Info.LogoutSuccessfulInfo;
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
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class that models a client that connects to the server in order to play the game though the use of a UI.
 */
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

    private final ExecutorService mainThread;

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
    private boolean isToReset;

    /**
     * Constructor for the Client
     * @param chosenUI represents a CLI or a GUI.
     */
    public Client(String chosenUI){
        isToReset = false;
        if(chosenUI != null){
            if(chosenUI.equals("cli"))
                UI = new CLI(this);
            if (chosenUI.equals("gui"))
                UI = new GUI(this);
        }
        defaultServerIP = "127.0.0.1";
        defaultServerPort = 4646;
        mainThread = Executors.newSingleThreadExecutor();
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
        mainThread.execute(() ->{
            isToReset = false;
            if(UI == null) //Skipped if --cli or --gui option
                askCLIorGUI();
            while (socket == null){ //Asks for server info
                Map<String, String> serverInfo = UI.requestServerInfo(defaultServerIP, defaultServerPort);
                serverIP = serverInfo.get("IP");
                try {
                    serverPort = Integer.parseInt(serverInfo.get("port"));
                }
                catch (NumberFormatException e){
                    serverPort = 0;
                }
                connectToLobbyServer(serverIP, serverPort);
            }

            startPing(); //Starts heartbeat with lobby server on another thread
            requestQueue.execute(() -> {
                nickname = UI.requestNickname();
                UI.setNickname(nickname);
                tryLobbyLogin();
            });

            while (! isToReset){
                Message message = receiveMessage();
                if (message != null)
                    parseMessage(message);
            }
        });
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
     * Tries to log in by sending a login user action with chosen username.
     */
    private void tryLobbyLogin(){
        LoginUserAction login;
        login = new LoginUserAction(nickname);
        sendUserAction(login);
    }

    /**
     * Disconnects user from lobby server.
     */
    private void disconnectFromLobby(){
        sendUserAction(new LobbyDisconnectUserAction(nickname));
        try {
            socket.close();
        } catch (IOException e) {
            fatalError("Error in disconnecting from server.");
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
            fatalError("Could not connect to match server.");
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
            fatalError("Error occurred while shutting down ping: "+ e.getLocalizedMessage());
        }
        if (error){
            fatalError("Error occurred while shutting down ping.");
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
        else if(message instanceof DisconnectionError){
            fatalError(message.toString());
        }
        else if (message instanceof Update){
            parseUpdate((Update) message);
        }
        else if (message instanceof Error){
            UI.displayError(message.toString(), false);
            parseUpdate(lastUpdate);
        }
        else if (message instanceof LogoutSuccessfulInfo){
            disconnectFromServer();
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

        if(update.getActionTakingPlayer() != null && update.getNextUserAction()!= null){
            if(update.getNextUserAction() != UserActionType.USE_ABILITY ||
                    (update.getNextUserAction() == UserActionType.USE_ABILITY && update.getGame().getActiveCharacterUsesLeft() >0 ))
                UI.displayInfo(update.getActionTakingPlayer() + " " + update.getNextUserAction().getActionToTake());
        }

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
                UI.requestWaitStart();
            }
            //During the game, a main thread is started that puts CLI/GUI in cycle waiting for an action to be chosen.
            //The possible actions are enabled and disabled by the client.
            case PLAY_ASSISTANT -> {
                toDisable.addAll(Arrays.asList(Command.END_TURN, Command.CHARACTER));
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
            case END_TURN -> {
                toDisable.add(Command.CLOUD);
                toEnable.add(Command.END_TURN);

                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
        }


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

        if(update.getActionTakingPlayer() != null && update.getNextUserAction()!= null){
            if(update.getNextUserAction() != UserActionType.USE_ABILITY ||
                    (update.getNextUserAction() == UserActionType.USE_ABILITY && update.getGame().getActiveCharacterUsesLeft() >0 ))
                UI.displayInfo(update.getActionTakingPlayer() + " " + update.getNextUserAction().getActionToTake());
        }
        //Info will be a different colored text in CLI or a pop-up in GUI, parallel to asking for action.
        //Allows information about other players action while user is selecting (useful for parallel login)
        List<Command> toDisable = new ArrayList<>();
        List<Command> toEnable = new ArrayList<>();
        switch (update.getNextUserAction()){
            case TOWER_COLOR -> {}
            case WIZARD, WAIT_GAME_START -> {
                UI.updateSetup(update.getGame(), update.getUserActionTaken());
            }
            case PLAY_ASSISTANT -> {
                toDisable.addAll(Arrays.asList(Command.ASSISTANT, Command.END_TURN, Command.CHARACTER, Command.ABILITY));
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
                toDisable.addAll(Arrays.asList(Command.ASSISTANT, Command.END_TURN, Command.CHARACTER, Command.ABILITY));
                UI.displayBoard(update.getGame(), update.getUserActionTaken());
                UI.updateCommands(toDisable, toEnable);
            }
            case MOVE_MOTHER_NATURE, USE_ABILITY, TAKE_FROM_CLOUD, END_TURN -> {
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
                List<String> winners = update.getGame().getPlayerTeams().entrySet().stream()
                        .filter(e -> e.getValue().equals(update.getGame().getWinner()))
                        .map(Map.Entry::getKey)
                        .toList();
                List<String> losers = update.getGame().getPlayerTeams().entrySet().stream()
                        .filter(e -> ! e.getValue().equals(update.getGame().getWinner()))
                        .map(Map.Entry::getKey)
                        .toList();
                UI.displayWinners(update.getGame().getWinner(), winners, losers);
                logoutFromServer();
            }
        }
    }

    //endregion

    //region Network send/receive

    /**
     * Sends a UserAction through the socket.
     * @param userAction the UserAction to be sent.
     */
    public synchronized void sendUserAction(UserAction userAction){
        try {
            outObj.writeObject(userAction);
            outObj.flush();
            outObj.reset();

        } catch (IOException e) {
            fatalError("Unable to write to server. " + userAction.getUserActionType());
        }
    }

    /**
     * Receives a message from the server through the socket.
     * @return the message received from the server, if converted successfully
     */
    private Message receiveMessage(){
        Object message;
        try {
            message = inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            if(e instanceof SocketTimeoutException){
                fatalError("Connection timed out.");
            }
            else if(e instanceof EOFException){
                fatalError("Someone disconnected.");
            }
            else{
                fatalError("Unable to receive messages from server.");
            }
            return null;
        }
        if(!(message instanceof Message)){
            fatalError("Server didn't send correct information.");
            return null;
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
    public void close() {
        System.exit(-1);
    }

    /**
     * Resets the client state and restarts main loop.
     */
    public void reset(){
        gameStarted = false;
        nickname = null;
        socket = null;

        start();
    }

    /**
     * If the client isn't being reset, it disconnects from server and forwards an error to be displayed to the user.
     * @param errorDescription the error to be displayed.
     */
    public void fatalError(String errorDescription){
        if(! isToReset){ //Ignores connection errors that try to reset client since client is already being reset
            disconnectFromServer();
            infoQueue.execute(() -> UI.displayError(errorDescription, true));
        }
    }

    /**
     * Logs out client from server by sending a logout user action.
     */
    private void logoutFromServer(){
        sendUserAction(new LogoutUserAction(nickname));
    }

    /**
     * Disconnects client from server.
     */
    private void disconnectFromServer(){
        isToReset = true;
        stopPing();
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
