package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Characters.AbstractCharacter;
import it.polimi.ingsw.GameModel.Characters.CharacterFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functions common to all concrete Characters
 */
class AbstractCharacterTest {

    /**
     * Tests that character is correctly created with its ID
     */
    @Test
    void getCharacterID() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char12 = factory.create(12, new Bag());
        assertEquals(12, char12.getCharacterID(),
                "Should be the same ID as requested at creation");
    }

    /**
     * Tests that firstUse is correctly kept track of
     */
    @Test
    void isFirstUse() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char11 = factory.create(11, new Bag());
        assertTrue(char11.isFirstUse(), "Character shouldn't have been used yet");
        char11.useCharacter(new Player());
        assertFalse(char11.isFirstUse(), "Character should have been used");
    }

    /**
     * Tests that character usage is correctly kept track of
     */
    @Test
    void wasUsedThisTurn() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char10 = factory.create(10, new Bag());
        assertFalse(char10.wasUsedThisTurn(), "Character shouldn't have been used this turn yet");
        char10.useCharacter(new Player());
        assertTrue(char10.wasUsedThisTurn(), "Character should have been used this turn");
        assertThrows(IllegalStateException.class, () -> char10.useCharacter(new Player()),
                "Character shouldn't be able to be used twice in same turn");
    }

    /**
     * Tests that cost is returned correctly, even after first use
     */
    @Test
    void getCost() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char9 = factory.create(9, new Bag());
        assertEquals(3, char9.getCost(), "Character 9 should cost 3 before use");
        char9.useCharacter(new Player());
        assertEquals(4, char9.getCost(), "Character 9 should cost 4 after use");
    }

    /**
     * Tests that method correctly resets use state of Character (used this turn and player)
     */
    @Test
    void resetUseState() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char8 = factory.create(8, new Bag());
        char8.useCharacter(new Player());
        assertTrue(char8.wasUsedThisTurn(), "Character should have been used this turn");
        assertNotNull(char8.getOwner(), "Character should have been used by a player");
        char8.resetUseState();
        assertFalse(char8.wasUsedThisTurn(), "Character shouldn't have been used this turn");
        assertNull(char8.getOwner(), "Character shouldn't have been used by a player");
    }

    /**
     * Tests that owner should coincide with player who used it.
     */
    @Test
    void getOwner() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char7 = factory.create(7, new Bag());
        Player placeholder = new Player();
        char7.useCharacter(placeholder);
        assertSame(placeholder, char7.getOwner(), "Owner should coincide with player who used it");
    }

    @Test
    void useAbility() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char6 = factory.create(6, new Bag());
        char6.useCharacter(new Player());
        char6.useAbility((list) -> char6.resetUseState(), new ArrayList<>());
        assertFalse(char6.wasUsedThisTurn());
    }

}