package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;

import java.io.Serializable;

/**
 *  Class that contains parameters to configure player attributes correctly for the given number of players.
 */
public class PlayerConfig implements Serializable {

    private int maxTowers,
    initialEntranceSize,
    movableEntranceStudents;

    private Bag bag;

    public PlayerConfig (int numOfPlayers) {
        switch (numOfPlayers) {
            case 2, 4 -> {
                this.maxTowers = 8;
                this.initialEntranceSize = 7;
                this.movableEntranceStudents = 3;
            }
            case 3 -> {
                this.maxTowers = 6;
                this.initialEntranceSize = 9;
                this.movableEntranceStudents = 4;
            }
        }
    }

    public int getMaxTowers() {
        return maxTowers;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public int getInitialEntranceSize() {
        return initialEntranceSize;
    }

    public int getMovableEntranceStudents() {
        return movableEntranceStudents;
    }
}
