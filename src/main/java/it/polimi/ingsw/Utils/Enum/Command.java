package it.polimi.ingsw.Utils.Enum;

import it.polimi.ingsw.Utils.CommandString;

/**
 * Enum that represent each command that the user can take.
 * Each command also has a string that represents the command to be written, a boolean to indicate whether the
 * command is used to perform game operations and a description of the command itself.
 */
public enum Command {
    HELP(CommandString.help, false,
            "Displays this screen."),
    TABLE(CommandString.table, false,
            "Displays the table of the game."),
    ORDER(CommandString.order, false,
            "Displays the current playing order."),
    CARDS(CommandString.cards, false,
            "Displays the assistants that each player can still play."),
    CHARACTER_INFO(CommandString.characterInfo, false,
            "Displays the characters that can be activated, along with a description of their effect."),
    ASSISTANT(CommandString.assistant, true,
            "Lets you play an assistant from you hand.\n" +
            " The assistant number will determine the order of the action phase (from lowest to highest).\n" +
            " The assistant move pover (M.P.) will determine the maximum amount of steps mother nature can take during your action phase."),
    MOVE(CommandString.move, true,
            "Lets you move a student from your entrance.\n" +
            " You can select any of your entrance student and then decide to move them to an island or to your dining room."),
    MOTHER_NATURE(CommandString.motherNature, true,
            "Lets you move mother nature.\n" +
            " The island where mother nature is move to will be resolved."),
    CLOUD(CommandString.cloud, true,
            "Lets you choose a cloud.\n" +
            " You can choose any cloud that is not empty and the students contained will be moved to your entrance."),
    CHARACTER(CommandString.character, true,
            "Lets you activate a character by using your coins.\n" +
            " You can only activate a character if you have enough coins."),
    ABILITY(CommandString.ability, true,
            "Lets you use the ability of a character that was previously activated.\n" +
            " Each ability may require different actions to be taken, type " + CommandString.characterInfo + " to know more."),
    END_TURN(CommandString.endTurn, true,
            "Ends the turn"),
    QUIT(CommandString.quit,false,
            "Quits the game, closing the match for each player connected");

    private final String commandToWrite;

    private final boolean isGameCommand;

    private final String help;

    Command(String commandToWrite, boolean isGameCommand, String help) {
        this.commandToWrite = commandToWrite;
        this.isGameCommand = isGameCommand;
        this.help = help;
    }

    public String getCommandToWrite(){
        return commandToWrite;
    }

    public boolean isGameCommand(){
        return isGameCommand;
    }

    public String getHelp() {
        return help;
    }
}
