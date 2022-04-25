package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Characters.CharacterFactory;
import it.polimi.ingsw.GameModel.Characters.NoEntryCharacter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NoEntryCharacter specific functions
 */
class NoEntryCharacterTest {

    /**
     * Tests character creation of a NoEntryCharacter
     */
    @Test
    void useCharacter() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5, new Bag());
    }


    /**
     * Tests functionality of no entry tiles, ensuring their numbering is correct and consistent
     */
    @Test
    void noEntryTilesTest() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5, new Bag());
        assertEquals(4, char5.getNoEntryTiles());
        char5.removeNoEntryTile();
        assertEquals(3, char5.getNoEntryTiles());
        char5.addNoEntryTile();
        assertEquals(4, char5.getNoEntryTiles());
        assertThrows(IllegalStateException.class, char5::addNoEntryTile);
        try {
            int i = 10;
            while (i > 0) {
                char5.removeNoEntryTile();
                i--;
            }
        } catch (Exception e) {
            assertEquals(char5.getNoEntryTiles(), 0);
            assertSame(e.getClass(), IllegalStateException.class);
        }
    }

    /**
     * Tests that resetting the useState correctly lets the character be used again
     */
    @Test
    void resetUseState() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5, new Bag());
        char5.removeNoEntryTile();
        char5.useCharacter(new Player());
        char5.resetUseState();
        assertEquals(char5.getNoEntryTiles(), 3); //no entry tiles unchanged
        assertNull(char5.getOwner());
    }
}