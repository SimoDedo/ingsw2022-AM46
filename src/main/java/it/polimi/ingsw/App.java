package it.polimi.ingsw;

import java.util.Arrays;
import java.util.List;

public class App {

    public static void  main(String[] args){
        List<String> argsList = Arrays.stream(args).toList();

        if(argsList.contains("--server"))
            ServerApp.main(args);
        else if (argsList.contains("--client") || argsList.contains("--cli") || argsList.contains("--gui"))
            ClientApp.main(args);
        else
            ClientApp.main(args);
    }
}
