package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;

/**
 * Class that contains a few parameters to configure the game correctly for the given number of
 * players. Consider encapsulating PlayerConfig inside GameConfig, so as to pass a single
 * wrapper parameter from Game down to the GameBoard.
 */
public class GameConfig {

    private int numOfPlayers,
    numOfClouds,
    cloudSize;
    private PlayerConfig playerConfig;

    public GameConfig(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        this.numOfClouds = numOfPlayers;
        switch (numOfPlayers) {
            case 2: case 4:
                this.cloudSize = 4;
                break;
            case 3:
                this.cloudSize = 3;
                break;
        }
        playerConfig = new PlayerConfig(numOfPlayers);
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public int getNumOfClouds() {
        return numOfClouds;
    }

    public int getCloudSize() {
        return cloudSize;
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public void setBag(Bag bag) {
        playerConfig.setBag(bag);
    }
}
