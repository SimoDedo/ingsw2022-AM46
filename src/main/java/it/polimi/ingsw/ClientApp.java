package it.polimi.ingsw;

import it.polimi.ingsw.View.Client;

import java.util.Arrays;
import java.util.List;

public class ClientApp {
    public static void main(String[] args){
        Boolean defaultServer = false;
        String UI = null;

        for (int i = 0; i < args.length; i++) {
            List<String> argsList = Arrays.stream(args).toList();
            if(argsList.contains("--default"))
                defaultServer = true;
            if(argsList.contains("--cli"))
                UI = "cli";
            else if(argsList.contains("--gui"))
                UI = "gui";
        }
        Client client = new Client(defaultServer, UI);

        client.start();
    }
}
