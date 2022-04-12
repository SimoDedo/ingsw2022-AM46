package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.Map;


public class GameFactory {

    public GameFactory() {}

    public Game create(int numOfPlayers, GameMode gameMode, Map<String, TowerColor> teamComposition) {
        switch (gameMode) {
            case NORMAL: default: return new Game(new GameConfig(numOfPlayers), teamComposition);
            case EXPERT: return new GameExpert(new GameConfig(numOfPlayers), teamComposition);
        }
    }
}
