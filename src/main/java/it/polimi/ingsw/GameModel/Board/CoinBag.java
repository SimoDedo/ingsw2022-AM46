package it.polimi.ingsw.GameModel.Board;

public class CoinBag {

    private int coins;

    public CoinBag() {
        coins = 20;
    }

    public void removeCoin() {
        coins--;
    }

    public void removeCoins(int numOfCoins) throws ArithmeticException {
        if (coins - numOfCoins < 0) {
            throw new ArithmeticException("Amount of coins taken exceeds current amount of coins");
        }
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
