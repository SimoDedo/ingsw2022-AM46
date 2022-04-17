package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.Map;


public class GameFactory {

    public GameFactory() {}

    public Game create(int numOfPlayers, GameMode gameMode) {
        return switch (gameMode) {
            case NORMAL -> new Game(new GameConfig(numOfPlayers));
            case EXPERT -> new GameExpert(new GameConfig(numOfPlayers));
        };
    }
}
