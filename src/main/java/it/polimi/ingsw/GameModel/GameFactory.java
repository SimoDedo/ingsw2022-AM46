package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;


/**
 * Class that implements the factory pattern to create different games.
 */
public class GameFactory {

    public GameFactory() {}

    /**
     * Creates a game with the given amount of players and the given game mode.
     * @param numOfPlayers the number of players that will play.
     * @param gameMode the game mode of the game.
     * @return the game that was created.
     */
    public Game create(int numOfPlayers, GameMode gameMode) {
        return switch (gameMode) {
            case NORMAL -> new Game(new GameConfig(numOfPlayers));
            case EXPERT -> new GameExpert(new GameConfig(numOfPlayers));
        };
    }
}
