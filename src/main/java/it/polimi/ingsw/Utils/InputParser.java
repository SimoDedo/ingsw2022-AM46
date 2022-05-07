package it.polimi.ingsw.Utils;

import java.util.Scanner;

public class InputParser {

    private final Scanner input;


    public InputParser(){
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
                System.out.println("Invalid input - please input an integer.");
            }
            input.next();
        }
        return input.nextInt();
    }



}
