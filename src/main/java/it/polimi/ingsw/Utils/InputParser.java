package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Utils.Exceptions.HelpException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Scanner;

public class InputParser {

    private final Scanner input;


    public InputParser(){
        this.input = new Scanner(System.in);
    }

    public String readLine() throws HelpException{
        synchronized (input){
            String line = input.nextLine();
            if(line.equalsIgnoreCase("help")) throw new HelpException();
            return line;
        }
    }

    public int readNumber() throws HelpException{
        boolean found = false;
        int number = 0;
        while(!found){
            String input = readLine();
            try {
                number = Integer.parseInt(input);
                found = true;
            }
            catch (NumberFormatException e){
                System.out.println("Invalid input - please input an integer.");
            }
        }
        return number;
    }

    public int readBoundNumber(int lo, int hi) throws HelpException{
        int number = readNumber();
        while(number < lo || number > hi){
            System.out.printf("The number must be between %d and %d%n", lo, hi);
            number = readNumber();
        }
        return number;
    }

    public int readNumberFromSelection(Collection<Integer> choices) throws HelpException{
        int number = readNumber();
        while(!choices.contains(number)){
            System.out.println("The number must be one of the following: " + Arrays.toString(choices.toArray()));
            number = readNumber();
        }
        return number;
    }

    public String readLineFromSelection(Collection<String> choices) throws HelpException{
        choices = choices.stream().map(String::toUpperCase).toList();
        String line = readLine();
        while(!choices.contains(line.toUpperCase())){
            System.out.println("The string must be one of the following: " + Arrays.toString(choices.toArray()));
            line = readLine();
        }
        return line;
    }



}
