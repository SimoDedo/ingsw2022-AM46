package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class NumOfPlayersUserAction extends UserAction{

    private int numOfPlayers;

    public NumOfPlayersUserAction(String sender, int numOfPlayers) throws IllegalArgumentException{
        super(sender, UserActionType.NUM_OF_PLAYERS);
        if(numOfPlayers<2 || numOfPlayers>4)
            throw  new IllegalArgumentException("Game with "+ numOfPlayers +"players is not supported");
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
