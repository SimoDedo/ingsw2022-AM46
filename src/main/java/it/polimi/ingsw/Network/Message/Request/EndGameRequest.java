package it.polimi.ingsw.Network.Message.Request;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.UserActionType;

public class EndGameRequest extends Request{

    private TowerColor winner;

    public EndGameRequest(String recipient, String request, UserActionType expectedUserAction, TowerColor winner) {
        super(recipient, request, expectedUserAction);
        this.winner = winner;
    }

    public TowerColor getWinner() {
        return winner;
    }
}
