package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.GameMode;

public class GameSettingInfo extends Info{

    private int numOfPlayersChosen;

    private GameMode gameModeChosen;

    public GameSettingInfo(int numOfPlayersChosen, GameMode gameModeChosen) {
        super("Game settings chosen for this game:");
        this.numOfPlayersChosen = numOfPlayersChosen;
        this.gameModeChosen = gameModeChosen;
    }

    public int getNumOfPlayersChosen() {
        return numOfPlayersChosen;
    }

    public GameMode getGameModeChosen() {
        return gameModeChosen;
    }

    @Override
    public String toString() {
        return super.toString() + "\nNumber of players: " + numOfPlayersChosen +
                "\nGame mode: " + gameModeChosen;
    }
}
