package it.polimi.ingsw;

import java.util.Arrays;
import java.util.List;

/**
 * Main class of the project that is run whenever the jar is started.
 * It is used to start both the client and the server using different parameters.
 * When ran without parameters, a client GUI is started.
 * Parameters:
 * --gui : starts a client GUI
 * --cli : starts a client CLI
 * --server : starts a server
 */
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
