package it.polimi.ingsw.GameModel.Board;

public class CoinBag {

    private int coins;

    public CoinBag() {
        coins = 20;
    }

    public void removeCoin() {
        coins--;
    }

    public void removeCoins(int numOfCoins) {
        coins -= numOfCoins; // will throw exception
    }

    public void addCoin() {
        coins++;
    }

    public void addCoins(int numOfCoins) {
        coins += numOfCoins;
    }

    public int getCoins() {
        return coins;
    }
}
