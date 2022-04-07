package it.polimi.ingsw.GameModel.Board;

/**
 * This class models a bag of coins. The coins are stored as a simple integer, and taking or placing
 * coins from the bag means increasing or decreasing that integer. The coin bag is used in the
 * advanced version of Game.
 */
public class CoinBag {

    private int coins;

    /**
     * Constructor for the class, which sets the initial amount of coins to 20.
     */
    public CoinBag() {
        coins = 20;
    }

    /**
     * Removes a coin from the bag.
     * @throws ArithmeticException if there are no coins inside the bag
     */
    public void removeCoin() throws ArithmeticException{
        if(coins == 0) throw
                new ArithmeticException("No more coins to take");
        coins--;
    }

    /**
     * Removes a given number of coins from the bag.
     * @param numOfCoins number of coins to take
     * @throws ArithmeticException if there are no coins inside the bag
     */
    public void removeCoins(int numOfCoins) throws ArithmeticException {
        if (coins - numOfCoins < 0) {
            throw new ArithmeticException("Amount of coins taken exceeds current amount of coins");
        }
        coins -= numOfCoins;
    }

    /**
     * Places a coin inside the bag.
     */
    public void addCoin() {
        coins++;
    }

    /**
     * Places a given number of coins inside the bag.
     * @param numOfCoins coins to place in the bag
     */
    public void addCoins(int numOfCoins) {
        coins += numOfCoins;
    }

    /**
     * Getter for the current number of coins in the bag.
     */
    public int getNumOfCoins() {
        return coins;
    }
}
