package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;

public class PlayerConfig {

    private int maxTowers,
    initialEntranceSize,
    movableEntranceStudents;

    private Bag bag;

    public PlayerConfig (int numOfPlayers) {
        switch (numOfPlayers) {
            case 2: case 4:
                this.maxTowers = 8;
                this.initialEntranceSize = 7;
                this.movableEntranceStudents = 3;
                break;
            case 3:
                this.maxTowers = 6;
                this.initialEntranceSize = 9;
                this.movableEntranceStudents = 4;
                break;
        }
    }

    public int getMaxTowers() {
        return maxTowers;
    }

    public void setMaxTowers(int maxTowers) {
        this.maxTowers = maxTowers;
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
