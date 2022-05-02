package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Info.*;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.CLI.CLI;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ClientDraft {

    private View view;

    private String nickname;

    private Socket socket;
    private OutputStream out;
    private ObjectOutputStream outObj;
    private InputStream in;
    private ObjectInputStream inObj;

    private int numOfPlayers;
    private GameMode gameMode;


    public ClientDraft(){
        numOfPlayers = 0;
        gameMode = null;
    }

    public void start(){

        askCLIorGUI();

        nickname = view.askLogin();
        lobbyConnection();
        ServerLoginInfo serverLoginInfo = tryLobbyLogin();
        while (serverLoginInfo == null){
            nickname = view.askLogin();
            clearScreen();
            serverLoginInfo = tryLobbyLogin();
        }
        view.setNickname(nickname);
        disconnectFormLobby();
        matchConnection("127.0.0.1", serverLoginInfo.getPort());

        gameSetup();
        view.showInfo((GameStartInfo) receiveMessage());
        gameLoop();
    }

    private void gameSetup(){
        GameJoinInfo gameJoinInfo = (GameJoinInfo) receiveMessage();
        if(gameJoinInfo.getLoggedPlayers().size() == 1){
            GameSettingsUserAction gameSettings = view.askGameSettings();
            sendUserAction(gameSettings);
        }

        GameSettingInfo gameSettingInfo = (GameSettingInfo) receiveMessage();
        view.showInfo(gameSettingInfo);
        numOfPlayers = gameSettingInfo.getNumOfPlayersChosen();
        gameMode = gameSettingInfo.getGameModeChosen();

        TowerColorUserAction towerColor = view.askTowerColor(numOfPlayers);
        sendUserAction(towerColor);
        view.showInfo((TowerColorInfo) receiveMessage());

        WizardUserAction wizard = view.askWizard();
        sendUserAction(wizard);
        view.showInfo((WizardInfo) receiveMessage());
    }

    private void gameLoop() {
        Message message = receiveMessage();
        while (! (message instanceof WinnerInfo)){

        }
    }



    private void lobbyConnection(){
        try{
            socket = new Socket("127.0.0.1", 4646);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            System.out.println("Connected to lobby server.");
        } catch (IOException e) {
            System.err.println("Could not connect to server. Error: " + e);
            System.exit(-1);
        }
    }

    private ServerLoginInfo tryLobbyLogin(){
        ServerLoginInfo serverLoginInfo = null;
        LoginUserAction login = new LoginUserAction(nickname);
        sendUserAction(login);
        Message message = receiveMessage();
        if(message instanceof ServerLoginInfo){
            view.showInfo((ServerLoginInfo)message);
            serverLoginInfo = (ServerLoginInfo) message;
        }
        else if(message instanceof Error){
            System.out.println("The following error occurred:\n " + message +"\n Please retry.");
        }
        else{
            System.out.println("Server didn't send correct information.");
            System.exit(-1);
        }

        return serverLoginInfo;
    }

    private void disconnectFormLobby(){
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error in disconnecting from server");
        }
    }

    private void matchConnection(String IP, int port){
        try{
            socket = new Socket(IP, port);
            out = socket.getOutputStream();
            outObj = new ObjectOutputStream(out);
            in = socket.getInputStream();
            inObj  = new ObjectInputStream(in);
            System.out.println("Connected to match server.");
        } catch (IOException e) {
            System.err.println("Could not connect to match server. Error: " + e);
            System.exit(-1);
        }
        sendUserAction(new LoginUserAction(nickname));
    }


    private void askCLIorGUI(){
        Scanner sysIn = new Scanner(System.in);
        int viewChoice = 0;
        while (viewChoice != 1 && viewChoice != 2){
            System.out.println("Select which interface you would prefer to play with:\n1.CLI\n2.GUI");
            viewChoice = sysIn.nextInt();
            sysIn.nextLine();
            clearScreen();
            switch (viewChoice){
                case 1 -> {
                    view = new CLI();
                    System.out.println("CLI selected!");
                }
                case 2 -> {
                    view = new CLI(); //FIXME: fix when GUI done
                    System.out.println("GUI selected!");
                }
                default -> {
                    System.out.println("Invalid choice! Please, select again.\n");
                }
            }
        }
    }


    //Network specific methods
    private void sendUserAction(UserAction userAction){
        try {
            outObj.writeObject(userAction);
        } catch (IOException e) {
            System.out.println("Unable to write to server.");
            System.exit(-1);
        }
    }

    private Message receiveMessage(){
        Object message = null;
        try {
            message = inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to receive messages from server");
            System.exit(-1);
        }
        if(!(message instanceof Message)){
            System.out.println("Server sending wrong objects");
            System.exit(-1);
        }
        return (Message) message;
    }

    private void parseNextMessage(UserActionType expectedResponseTo){
        Object message = receiveMessage();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
