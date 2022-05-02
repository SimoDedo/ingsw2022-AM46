package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.Network.Message.Info.Info;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.View;

import java.util.Arrays;
import java.util.Scanner;

public class CLI implements View {

    private Scanner sysIn;

    private String nickname;

    public CLI() {
        sysIn = new Scanner(System.in);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String askLogin(){
        System.out.println("Input nickname:");
        return sysIn.nextLine();
    }

    public GameSettingsUserAction askGameSettings(){
        int numOfPlayers = 0;
        while (numOfPlayers < 2 || numOfPlayers > 4){
            System.out.println("Select how many player will participate in this game [2,3 or 4]:");
            numOfPlayers = sysIn.nextInt();
            sysIn.nextLine();
            clearScreen();
            if(numOfPlayers < 2 || numOfPlayers > 4){
                System.out.println("Invalid selection! Please select 2,3 or 4.");
            }
        }
        int gameModeChoice = 0;
        while (gameModeChoice < 1 || gameModeChoice > 2){
            System.out.println("Select game mode:\n1.Normal\n2.Expert");
            gameModeChoice = sysIn.nextInt();
            sysIn.nextLine();
            clearScreen();
            if(gameModeChoice < 1 || gameModeChoice > 2){
                System.out.println("Invalid selection! Please select 2,3 or 4.");
            }
        }
        return new GameSettingsUserAction(nickname,
                numOfPlayers,
                Arrays.stream(GameMode.values()).toList().get(gameModeChoice - 1));
    }

    public TowerColorUserAction askTowerColor(int numOfPlayers){
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
            towerChoice = sysIn.nextInt();
            sysIn.nextLine();
            clearScreen();
            if(towerChoice < 1 || towerChoice > maxChoice)
                System.out.println("Please choose a valid team color.");
        }
        return new TowerColorUserAction(nickname, Arrays.stream(TowerColor.values()).toList().get(towerChoice - 1));
    }

    public WizardUserAction askWizard(){
        int wizardChoice = 0;
        while (wizardChoice < 1 || wizardChoice > 4) {
            System.out.println("Choose your wizard:\n1.Mage\n2.King\n3.Witch\n4.samurai");
            wizardChoice = sysIn.nextInt();
            sysIn.nextLine();
            clearScreen();
            if(wizardChoice < 1 || wizardChoice > 4)
                System.out.println("Please choose a valid wizard.");
        }
        return new WizardUserAction(nickname, Arrays.stream(WizardType.values()).toList().get(wizardChoice - 1));
    }

    public void showInfo(Info info){
        System.out.println("\n"+info+"\n");
    }


    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
