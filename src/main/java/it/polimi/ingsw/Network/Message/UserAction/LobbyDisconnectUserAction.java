package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to disconnect from the lobby in order to login in the match server.
 */
public class LobbyDisconnectUserAction extends UserAction{
    public LobbyDisconnectUserAction(String sender) {
        super(sender, UserActionType.LOBBY_DISCONNECT);
    }
}
