package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;

import java.io.Serializable;

/**
 * Class that contains parameters to configure the game correctly for the given number of players.
 */
public class GameConfig implements Serializable {

    private final int numOfPlayers;
    private final int numOfClouds;
    private int cloudSize;
    private final PlayerConfig playerConfig;

    public GameConfig(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        this.numOfClouds = numOfPlayers;
        switch (numOfPlayers) {
            case 2, 4 -> this.cloudSize = 3;
            case 3 -> this.cloudSize = 4;
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

}
