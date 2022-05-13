package it.polimi.ingsw.GameModel.Board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CoinBag class
 */
class CoinBagTest {

    final int initialCoins = 20;

    /**
     * Test that it only removes one coin from starting state
     */
    @Test
    void removeCoin() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoin();
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart - 1, "unexpected number of coins left in bag after removal");
    }
    /**
     * Test that it removes exact number of coins from starting state
     */
    @Test
    void removeCoins() {
        CoinBag coinBag = new CoinBag(initialCoins);
        int numOfCoinsStart = coinBag.getNumOfCoins();
        coinBag.removeCoins(10);
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart - 10, "unexpected number of coins left in bag after removal");
    }

    /**
     * Test that it throws exceptions when coin bag is empty
     */
    @Test
    void removeCoinsException(){
        CoinBag coinBag = new CoinBag(initialCoins);
        assertThrows(ArithmeticException.class, () -> coinBag.removeCoins(coinBag.getNumOfCoins() + 1),
                "no exception raised when removing more coins than what the bag is holding");
        coinBag.removeCoins(coinBag.getNumOfCoins());
        assertThrows(ArithmeticException.class, coinBag::removeCoin,
                "no exception raised when removing coin from empty bag");
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
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart, "either removeCoin or addCoin did not do their job properly");

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
        assertEquals(coinBag.getNumOfCoins(), numOfCoinsStart,
                "unexpected number of coins in bag after removal and addition of multiple at a time");
    }
}