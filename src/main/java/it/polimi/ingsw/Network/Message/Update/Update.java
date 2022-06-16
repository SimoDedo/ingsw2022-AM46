package it.polimi.ingsw.Network.Message.Update;

import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * Main class that represent the server's response to a Client UserAction.
 * The game state is sent in its entirety through the use of an interface which exposes only observer methods.
 */
public class Update extends Message {

    /**
     * The current game state
     */
    private final ObservableByClient game;

    /**
     * The UserAction that triggered this update to be sent
     */
    private final UserActionType userActionTaken;

    /**
     * The player who took the UserActionTaken
     */
    private final String playerActionTaken;

    /**
     * The action that is expected from the action taking player.
     */
    private final UserActionType nextUserAction;

    /**
     * String that represents the player who should take the action expected in this update.
     * All other players will treat use this update only to show information
     */
    private final String actionTakingPlayer;

    private final String info;

    public Update(ObservableByClient game,String playerActionTaken, UserActionType userActionTaken,
                  String actionTakingPlayer, UserActionType nextUserAction, String info) {
        super("Server");
        this.game = game;
        this.userActionTaken = userActionTaken;
        this.playerActionTaken = playerActionTaken;
        this.nextUserAction = nextUserAction;
        this.actionTakingPlayer = actionTakingPlayer;
        this.info = info;
    }

    public ObservableByClient getGame() {
        return game;
    }

    public UserActionType getUserActionTaken() {
        return userActionTaken;
    }

    public String getPlayerActionTaken() {
        return playerActionTaken;
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

    @Override
    public String toString() {
        return info;
    }
}
