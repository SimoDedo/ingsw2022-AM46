package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to select the game setting.
 */
public class GameSettingsUserAction extends UserAction{

    /**
     * The number of players chosen.
     */
    private final int numOfPlayers;

    /**
     * The game mode chosen.
     */
    GameMode gameMode;


    public GameSettingsUserAction(String sender, int numOfPlayers, GameMode gameMode){
        super(sender, UserActionType.GAME_SETTINGS);
        this.numOfPlayers = numOfPlayers;
        this.gameMode = gameMode;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

}
