package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public class GameModeUserAction extends UserAction{

    GameMode gameMode;

    public GameModeUserAction(String sender, GameMode gameMode) {
        super(sender, UserActionType.GAME_MODE);
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
