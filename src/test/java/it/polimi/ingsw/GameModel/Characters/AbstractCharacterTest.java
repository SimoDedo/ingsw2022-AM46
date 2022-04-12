package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Characters.AbstractCharacter;
import it.polimi.ingsw.GameModel.Characters.CharacterFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCharacterTest {

    @Test
    void getCharacterID() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char12 = factory.create(12);
        assertEquals(char12.getCharacterID(), 12);
    }

    @Test
    void isFirstUse() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char11 = factory.create(11);
        assertTrue(char11.isFirstUse());
        char11.useCharacter(new Player());
        assertFalse(char11.isFirstUse());
    }

    @Test
    void wasUsedThisTurn() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char10 = factory.create(10);
        assertFalse(char10.wasUsedThisTurn());
        char10.useCharacter(new Player());
        assertTrue(char10.wasUsedThisTurn());
        assertThrows(IllegalStateException.class, () -> char10.useCharacter(new Player()));
    }

    @Test
    void getCost() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char9 = factory.create(9);
        assertEquals(char9.getCost(), 3);
        char9.useCharacter(new Player());
        assertEquals(char9.getCost(), 4);
    }

    @Test
    void resetUseState() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char8 = factory.create(8);
        char8.useCharacter(new Player());
        assertTrue(char8.wasUsedThisTurn());
        assertNotNull(char8.getOwner());
        char8.resetUseState();
        assertFalse(char8.wasUsedThisTurn());
        assertNull(char8.getOwner());
    }

    @Test
    void getOwner() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char7 = factory.create(7);
        Player placeholder = new Player();
        char7.useCharacter(placeholder);
        assertSame(placeholder, char7.getOwner());
    }

    @Test
    void useAbility() {
        CharacterFactory factory = new CharacterFactory();
        AbstractCharacter char6 = factory.create(6);
        char6.useCharacter(new Player());
        char6.useAbility((list) -> char6.resetUseState(), new ArrayList<>());
        assertFalse(char6.wasUsedThisTurn());
    }

}