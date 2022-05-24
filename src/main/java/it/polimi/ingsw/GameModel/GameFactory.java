package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;


public class GameFactory {

    public GameFactory() {}

    public Game create(int numOfPlayers, GameMode gameMode) {
        return switch (gameMode) {
            case STANDARD -> new Game(new GameConfig(numOfPlayers));
            case EXPERT -> new GameExpert(new GameConfig(numOfPlayers));
        };
    }
}
