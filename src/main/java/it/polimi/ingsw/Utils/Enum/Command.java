package it.polimi.ingsw.Utils.Enum;

import it.polimi.ingsw.Utils.CommandString;

public enum Command {
    HELP(CommandString.help, false),
    TABLE(CommandString.table, false),
    ORDER(CommandString.order, false),
    CARDS(CommandString.cards, false),
    ASSISTANT(CommandString.assistant, true),
    MOVE(CommandString.move, true),
    MOTHER_NATURE(CommandString.motherNature, true),
    CLOUD(CommandString.cloud, true),
    CHARACTER(CommandString.character, true),
    END_TURN(CommandString.endTurn, true),
    QUIT(CommandString.quit,false);

    private final String commandToWrite;

    private final boolean isGameCommand;

    Command(String commandToWrite, boolean isGameCommand) {
        this.commandToWrite = commandToWrite;
        this.isGameCommand = isGameCommand;
    }

    public String getCommandToWrite(){
        return commandToWrite;
    }

    public boolean isGameCommand(){
        return isGameCommand;
    }
}
