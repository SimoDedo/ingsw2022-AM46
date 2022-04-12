package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.Map;


public class GameFactory {

    public GameFactory() {}

    public Game create(int numOfPlayers, GameMode gameMode, Map<String, TowerColor> teamComposition) {
        return switch (gameMode) {
            case NORMAL -> new Game(new GameConfig(numOfPlayers), teamComposition);
            case EXPERT -> new GameExpert(new GameConfig(numOfPlayers), teamComposition);
        };
    }
}
