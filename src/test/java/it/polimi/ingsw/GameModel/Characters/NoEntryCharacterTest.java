package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Characters.CharacterFactory;
import it.polimi.ingsw.GameModel.Characters.NoEntryCharacter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoEntryCharacterTest {

    @Test
    void useCharacter() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5);
    }

    @Test
    void useAbility() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5);
    }

    @Test
    void noEntryTilesTest() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5);
        assertEquals(4, char5.getNoEntryTiles());
        char5.removeNoEntryTile();
        assertEquals(3, char5.getNoEntryTiles());
        char5.addNoEntryTile();
        assertEquals(4, char5.getNoEntryTiles());
        assertThrows(IllegalStateException.class, char5::addNoEntryTile);
        try {
            while (true) {
                char5.removeNoEntryTile();
            }
        } catch (Exception e) {
            assertEquals(char5.getNoEntryTiles(), 0);
            assertSame(e.getClass(), IllegalStateException.class);
        }
    }

    @Test
    void resetUseState() {
        CharacterFactory factory = new CharacterFactory();
        NoEntryCharacter char5 = (NoEntryCharacter) factory.create(5);
        char5.removeNoEntryTile();
        char5.useCharacter(new Player());
        char5.resetUseState();
        assertEquals(char5.getNoEntryTiles(), 3); //no entry tiles unchanged
        assertNull(char5.getOwner());
    }
}