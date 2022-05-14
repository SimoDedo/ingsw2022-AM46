package it.polimi.ingsw.View;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Info.ServerLoginInfo;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.Update.Update;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.View.CLI.CLI;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {

    private String serverIP;
    private int serverPort;
    private final boolean usesDefaultServer;

    private UI UI;

    private String nickname;

    private Update lastUpdate;

    private Socket socket;
    private OutputStream out;
    private ObjectOutputStream outObj;
    private InputStream in;
    private ObjectInputStream inObj;

    private ExecutorService askQueue;
    private ExecutorService infoQueue;
    private Ping ping;
    private ExecutorService pingExecutor;
    private Thread mainThread;

    public Client(Boolean usesDefaultServer, String chosenUI){
        if(chosenUI != null){
            if(chosenUI.equals("cli"))
                UI = new CLI(this);
            if (chosenUI.equals("gui"))
                UI = new CLI(this); //FIXME: gui here
        }
        askQueue = Executors.newSingleThreadExecutor();
        infoQueue = Executors.newSingleThreadExecutor();
        serverIP = "127.0.0.1";
        serverPort = 4646;
        this.usesDefaultServer = usesDefaultServer;
        mainThread = new Thread(() -> UI.parseCommand());
    }


    public void start(){
        if(UI == null) //Skipped if --cli or --gui option
            askCLIorGUI();

        while (socket == null){ //Asks for server info
            if(!usesDefaultServer){ //Skipped only if --default option
                Map<String, String> serverInfo = UI.askServerInfo();
                serverIP = serverInfo.get("IP");
                serverPort = Integer.parseInt(serverInfo.get("port"));
            }
            else
                UI.askTryConnecting();
            lobbyConnection(serverIP, serverPort);
        }
        startPing(); //Starts heartbeat with lobby server

        ServerLoginInfo serverLoginInfo = null;
        while (serverLoginInfo == null){ //Asks for login username (until valid one is given)
            nickname = UI.askNickname();
            serverLoginInfo = tryLobbyLogin();
        }
        UI.setNickname(nickname);

        stopPing(); //Stops heartbeat with lobby server to avoid writing on the socket that is subsequently closed
        disconnectFromLobby();
        matchLogin(serverIP, serverLoginInfo.getPort());
        startPing(); //Starts heartbeat with match server

        while (true){
            parseMessage( receiveMessage() );
        }

    }

    //region Server connection

    private void lobbyConnection(String IP, int port){
        try{
            socket = new Socket(IP, port);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            UI.showText("Connected to lobby server.");
        } catch (IOException e) {
            UI.showError("Could not connect to server.");
        }
    }

    private ServerLoginInfo tryLobbyLogin(){
        ServerLoginInfo serverLoginInfo = null;
        LoginUserAction login;
        try {
            login = new LoginUserAction(nickname);
        }
        catch (IllegalArgumentException e){
            UI.showError("Enter a nickname that isn't empty!");
            return null;
        }
        sendUserAction(login);
        Message message = receiveMessage();
        if(message instanceof ServerLoginInfo){
            UI.showText("Connecting to port: "+ ((ServerLoginInfo) message).getPort());
            serverLoginInfo = (ServerLoginInfo) message;
        }
        else if(message instanceof Error){
            UI.showError(message.toString());
        }
        else{
            UI.showError("Server didn't send correct information.");
            reset();
        }

        return serverLoginInfo;
    }

    private void disconnectFromLobby(){
        try {
            socket.close();
        } catch (IOException e) {
            UI.showError("Error in disconnecting from server");
        }
    }

    private void matchLogin(String IP, int port){
        try{
            socket = new Socket(IP, port);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            UI.showText("Connected to match server.");
        } catch (IOException e) {
            UI.showError("Could not connect to match server. Error: " + e);
            reset();
        }
        sendUserAction(new LoginUserAction(nickname));
    }

    private void startPing(){
        ping = new Ping(this);
        pingExecutor = Executors.newSingleThreadExecutor();
        pingExecutor.execute(ping);
    }

    private void stopPing(){
        pingExecutor.shutdownNow();
        boolean error;
        try { //Ensures ping was successfully stopped, to avoid error when socket is closed
            error = ! pingExecutor.awaitTermination(5, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            error = true;
        }
        if (error)
            UI.showError("Error occurred while shutting down ping.");
    }
    //endregion


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
                    UI = new CLI(this); //FIXME: fix when GUI done
                    System.out.println("GUI selected!");
                }
                default -> {
                    System.out.println("Invalid choice! Please, select again.\n");
                }
            }
        }
    }

    //region Parsing
    private void parseMessage(Message message){
        if (message instanceof Error){
            parseUpdate(lastUpdate);
            UI.showError(message.toString());
        }
        else if (message instanceof Update){
            parseUpdate((Update) message);
        }
        else{
            UI.showError("Server didn't send correct information.");
            reset();
        }
    }

    private void parseUpdate(Update update){
        UI.update(update.getGame());
        if(nickname.equals(update.getActionTakingPlayer()) && update.getNextUserAction() != null){
            lastUpdate = update;
            askAction(update);
        }
        else {
            showInfo(update);
        }
        if(mainThread.isAlive()){
            UI.displayAvailableCommands();
        }
        UI.notifyServerResponse();
    }

    private void askAction(Update update){
        switch (update.getNextUserAction()){
            //During setup, action are requested in a different thread to allow info on others' actions to be received
            case GAME_SETTINGS -> { //TODO: use "request" functions
                askQueue.execute(() -> UI.askGameSettings());
            }
            case TOWER_COLOR -> {
                askQueue.execute(() -> UI.askTowerColor(update.getGame().getNumOfPlayers()));
            }
            case WIZARD -> {
                askQueue.execute(() -> UI.askWizard());
            }
            //No input is accepted when here, client is waiting for others to complete their selection (and receive PLAY_ASSISTANT)
            //Commands are enabled but main thread isn't started yet. Commands are "pre-loaded".
            case NEXT_TURN_WAIT -> {
                UI.showInfo("Please wait for all players to be ready and for your turn to start...");
                UI.enableCommand(Command.QUIT);
                UI.enableCommand(Command.HELP);
                UI.enableCommand(Command.CARDS);
                UI.enableCommand(Command.TABLE);
                UI.enableCommand(Command.ORDER);
            }
            //During the game, a main thread is started that puts CLI/GUI in cycle waiting for an action to be chosen.
            //The possible actions are enabled and disabled by the client.
            case PLAY_ASSISTANT -> {
                UI.disableCommand(Command.CLOUD);
                UI.enableCommand(Command.ASSISTANT);
                if(!mainThread.isAlive())
                    mainThread.start();
                UI.standings();
                UI.showInfo("It is now your planning phase turn! Choose an assistant.");
            }
            case MOVE_STUDENT -> {
                UI.disableCommand(Command.ASSISTANT);
                UI.enableCommand(Command.MOVE);
                UI.standings();
                UI.showInfo("It is now your action phase turn! Move students");
            }
            case MOVE_MOTHER_NATURE -> {
                UI.disableCommand(Command.MOVE);
                UI.enableCommand(Command.MOTHER_NATURE);
                UI.standings();
                UI.showInfo("Move mother nature.");
            }
            case TAKE_FROM_CLOUD -> {
                UI.disableCommand(Command.MOTHER_NATURE);
                UI.enableCommand(Command.CLOUD);
                UI.standings();
                UI.showInfo("Choose a cloud.");
            }
            case USE_ABILITY -> {

            }
        }
    }

    private void showInfo(Update update){
        //Info will be a different colored text in CLI or a pop-up in GUI, parallel to asking for action.
        //Allows information about other players action while user is selecting (useful for parallel login)
        switch (update.getNextUserAction()){
            case TOWER_COLOR -> {
                infoQueue.execute(() -> UI.showInfo(update.getActionTakingPlayer() + " connected and is choosing a tower color"));
            }
            case WIZARD -> {
                infoQueue.execute(() -> UI.showInfo(
                        update.getActionTakingPlayer() +" chose color" ));
                infoQueue.execute(() -> UI.showInfo(update.getActionTakingPlayer() +" is now choosing a wizard" ));
            }
            case NEXT_TURN_WAIT -> {
                infoQueue.execute(() -> UI.showInfo(update.getActionTakingPlayer() +" is ready!" ));
            }
            case PLAY_ASSISTANT -> {
                UI.disableCommand(Command.ASSISTANT);
                UI.disableCommand(Command.CLOUD);
                if(!mainThread.isAlive())
                    mainThread.start();
                UI.standings();
                UI.showInfo(update.getActionTakingPlayer() +" is now choosing an assistant" );
            }
            case MOVE_STUDENT -> {
                UI.disableCommand(Command.ASSISTANT);
                UI.disableCommand(Command.CLOUD);
                UI.standings();
                UI.showInfo(update.getActionTakingPlayer() +" is now choosing which student to move" );
            }
            case MOVE_MOTHER_NATURE -> {
                UI.standings();
                UI.showInfo(update.getActionTakingPlayer() +" is now choosing where to move mother nature" );
            }
            case TAKE_FROM_CLOUD -> {
                UI.standings();
                UI.showInfo(update.getActionTakingPlayer() +" is now choosing a cloud" );
            }
            case USE_ABILITY -> {
                UI.showInfo(update.getActionTakingPlayer() +" has now activated a character" );
            }
            case END_GAME -> {
                for(Command command : Command.values()){
                    if(command != Command.QUIT)
                        UI.disableCommand(command);
                }
                UI.standings();
                UI.showInfo("TEAM " + update.getGame().getWinner() + " HAS WON!!");
            }
        }

    }
    //endregion

    //region Network send/receive
    public void sendUserAction(UserAction userAction){
        try {
            outObj.writeObject(userAction);
        } catch (IOException e) {
            UI.showError("Unable to write to server.");
            reset();
        }
    }

    private Message receiveMessage(){
        Object message = null;
        try {
            message = inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            UI.showError("Unable to receive messages from server");
            reset();
        }
        if(!(message instanceof Message)){
            UI.showError("Server sending wrong objects");
            reset();
        }
        return (Message) message;
    }
    //endregion

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void reset(){
        System.exit(-1); //FIXME: actual reset
        askQueue.shutdownNow();
        infoQueue.shutdownNow();
        askQueue = Executors.newSingleThreadExecutor();
        infoQueue = Executors.newSingleThreadExecutor();
        serverIP = "127.0.0.1";
        serverPort = 4646;
        if(UI instanceof CLI)
            UI = new CLI(this);
        else
            UI = new CLI(this); //FIXME: GUI here
        socket = null;
        start();
    }
}
