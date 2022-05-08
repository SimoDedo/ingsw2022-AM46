package it.polimi.ingsw.GameModel.Board;

import java.io.Serializable;

public class CoinBag implements Serializable {

    private int coins;

    public CoinBag(int initialCoins) {
        coins = initialCoins;
    }

    public void removeCoin() throws ArithmeticException {
        if (coins == 0) throw new ArithmeticException("No more coins to take");
        coins--;
    }

    public void removeCoins(int numOfCoins) throws ArithmeticException {
        if (coins - numOfCoins < 0) throw new ArithmeticException("Amount of coins taken exceeds current amount of coins");
        coins -= numOfCoins;
    }

    public void addCoin() {
        coins++;
    }

    public void addCoins(int numOfCoins) {
        coins += numOfCoins;
    }

    public int getNumOfCoins() {
        return coins;
    }
}
