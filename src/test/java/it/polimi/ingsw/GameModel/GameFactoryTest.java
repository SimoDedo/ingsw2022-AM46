package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameFactory class.
 */
class GameFactoryTest {

    @Test
    void createNormal() {
        GameFactory factory = new GameFactory();
        Game normal = factory.create(4, GameMode.NORMAL);
        assertSame(normal.getClass(), Game.class);
    }

    @Test
    void createExpert() {
        GameFactory factory = new GameFactory();
        Game expert = factory.create(4, GameMode.EXPERT);
        assertSame(expert.getClass(), GameExpert.class, "factory not working as expected: game is not GameExpert type");
    }

}