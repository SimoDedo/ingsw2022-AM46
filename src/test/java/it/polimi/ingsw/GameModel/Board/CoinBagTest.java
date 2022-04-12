package it.polimi.ingsw.GameModel.Board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CoinBag class
 */
class CoinBagTest {

    int initialCoins = 20;

    /**
     * Test that it only removes one coin from starting state
     */
    @Test
    void removeCoin() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoin();
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart - 1);
    }
    /**
     * Test that it removes exact number of coins from starting state
     */
    @Test
    void removeCoins() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoins(10);
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart - 10);
    }

    /**
     * Test that it throws exceptions when coin bag is empty
     */
    @Test
    void removeCoinsException(){
        CoinBag coinBag = new CoinBag(initialCoins);
        assertThrows(ArithmeticException.class, () -> coinBag.removeCoins(coinBag.getNumOfCoins() + 1));
        coinBag.removeCoins(coinBag.getNumOfCoins());
        assertThrows(ArithmeticException.class, coinBag::removeCoin);
    }

    /**
     * Test that it only adds one coin from starting state
     */
    @Test
    void addCoin() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoin();
        coinBag.addCoin();
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart);

    }

    /**
     * Test that it adds exact number of coins from starting state
     */
    @Test
    void addCoins() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoins(10);
        coinBag.addCoins(10);
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart);
    }
}