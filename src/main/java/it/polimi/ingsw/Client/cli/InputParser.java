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



}
