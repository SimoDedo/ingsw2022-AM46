package it.polimi.ingsw;

import it.polimi.ingsw.View.Client;

import java.util.Arrays;
import java.util.List;

public class ClientApp {
    public static void main(String[] args){
        String UI;
        List<String> argsList = Arrays.stream(args).toList();

        if(argsList.contains("--cli"))
            UI = "cli";
        else if(argsList.contains("--gui"))
            UI = "gui";
        else
            UI = "gui";

        Client client = new Client(UI);
        client.start();
    }
}

