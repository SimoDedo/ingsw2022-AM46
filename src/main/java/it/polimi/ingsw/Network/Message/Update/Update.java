package it.polimi.ingsw.Network.Message.Update;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public class Update extends Message {

    ObservableByClient game;

    /**
     * String that represents the player who should take the action requested in this update.
     * All other players will treat use this update only to show information
     */
    String actionTakingPlayer;

    /**
     * The action that is expected from the action taking player.
     */
    UserActionType nextUserAction;

    String info;

    public Update(ObservableByClient game, String actionTakingPlayer, UserActionType nextUserAction, String info) {
        super("Server");
        this.game = game;
        this.actionTakingPlayer = actionTakingPlayer;
        this.nextUserAction = nextUserAction;
        this.info = info;
    }

    public ObservableByClient getGame() {
        return game;
    }

    public String getActionTakingPlayer() {
        return actionTakingPlayer;
    }

    public UserActionType getNextUserAction() {
        return nextUserAction;
    }

    public String getInfo() {
        return info;
    }
}
