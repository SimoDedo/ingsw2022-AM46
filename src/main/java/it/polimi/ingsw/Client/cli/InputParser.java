package it.polimi.ingsw.Client.cli;

import it.polimi.ingsw.Client.Client;

import java.util.Scanner;

public class InputParser {

    private final CLI cli;
    private final Scanner input;


    public InputParser(CLI cli){
        this.cli = cli;
        this.input = new Scanner(System.in);
    }

    public String readLine() {
        synchronized (input){
            return input.nextLine();
        }
    }

    public int readNumber() {
        synchronized (input) {
            while(!input.hasNextInt()){
                cli.displayMessage("Invalid input - please input an integer.");
            }
            input.next();
        }
        return input.nextInt();
    }

    public void parseCommand(String input){

        String command = readLine();

        switch (command){
            case "": case "\n": break;
            case "help": cli.displayHelp(); break;
            case "choose cloud": cli.requestCloud(); break;
            case "move": cli.requestMove(); break;
            case "play assistant": cli.requestAssistant(); break;
            case "play character": cli.requestCharacter(); break;
            case "standings": cli.standings(); break;
            case "end turn": cli.requestEndTurn(); break;
            default: cli.displayMessage("Invalid command - please type help for a list of available commands."); break;

        }
    }


}
