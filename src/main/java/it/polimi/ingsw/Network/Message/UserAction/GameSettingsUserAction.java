package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public class GameSettingsUserAction extends UserAction{

    private final int numOfPlayers;

    GameMode gameMode;


    public GameSettingsUserAction(String sender, int numOfPlayers, GameMode gameMode) throws IllegalArgumentException{
        super(sender, UserActionType.GAME_SETTINGS);
        if(numOfPlayers<2 || numOfPlayers>4)
            throw  new IllegalArgumentException("Game with "+ numOfPlayers +"players is not supported");
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
