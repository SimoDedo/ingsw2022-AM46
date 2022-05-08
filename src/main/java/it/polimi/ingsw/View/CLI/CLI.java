package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.Client;
import it.polimi.ingsw.View.UI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI implements UI {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001b[31m";

    private Scanner sysIn;

    private String nickname;

    private Client client;

    public CLI(Client client) {
        sysIn = new Scanner(System.in);
        this.client = client;
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
        System.out.println(ANSI_YELLOW + info + ANSI_RESET + "\n");
    }

    @Override
    public void showError(String error) {
        System.out.println(ANSI_RED + error +"\nPlease retry." + ANSI_RESET);
    }


    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void reset(){
        Thread.currentThread().interrupt(); //FIXME: this does nothing wtf were u thinking, gets called by "main" thread, doesn't interrupt task
    }
}
