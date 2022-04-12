package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.RequestParameters;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests MoveCharacter class
 */
class MoveCharacterTest {

    /**
     * Tests that method correctly fills C1,C7,C11 container fully, and doesn't fill C10,C12
     */
    @Test
    void  initialFill(){
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(((MoveCharacter)character).pawnCount() == 4);
        character = characterFactory.create(7);
        assertTrue(((MoveCharacter)character).pawnCount() == 6);
        character = characterFactory.create(11);
        assertTrue(((MoveCharacter)character).pawnCount() == 4);
        character = characterFactory.create(10);
        assertTrue(((MoveCharacter)character).pawnCount() == 0);
        character = characterFactory.create(12);
        assertTrue(((MoveCharacter)character).pawnCount() == 0);
    }

    /**
     * Test that useCharacter creates character with expected parameters
     */
    @Test
    void useCharacter() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.useCharacter(1, null).get(0).equals(RequestParameters.STUDCARD));
    }

    /**
     * Tests that exception are thrown as expected
     */
    @Test
    void useCharacterExceptions() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        character.useCharacter(1, null);
        assertThrows(IllegalStateException.class, () -> character.useCharacter(1, null));
        assertThrows(IllegalArgumentException.class, () -> character.useCharacter(2, null));
    }

    /**
     * Tests that ID is correctly assigned
     */
    @Test
    void getCharacterID() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.getCharacterID()==1);
    }

    /**
     * Test that returns true only on first use
     */
    @Test
    void isFirstUse() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.isFirstUse());
        character.useCharacter(1, null);
        assertFalse(character.isFirstUse());
    }

    /**
     * Tests that returns true when used
     */
    @Test
    void wasUsedThisTurn() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        character.useCharacter(1, null);
        assertTrue(character.wasUsedThisTurn());
    }

    /**
     * Tests that it returns right amount of coin to pay even after being used the first time
     */
    @Test
    void getCost() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.getCost() == 1);
        character.useCharacter(1, null);
        assertTrue( character.getCost() == 2);
    }

    /**
     * Tests that it correctly resets the use state of the Character
     */
    @Test
    void resetUseState() {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        character.useCharacter(1, null);
        assertTrue(character.wasUsedThisTurn());
        character.resetUseState();
        assertFalse(character.wasUsedThisTurn());
    }

    /**
     * Tests that ability correctly functions the way it is expected. (Removes a student from card, gives it to selected island, draws no student on card)
     * @throws IllegalAccessException not tested here
     */
    @Test
    void useAbilityC1() throws IllegalAccessException {
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterFactory characterFactory = new CharacterFactory(bag, new PlayerList());
        Character character = characterFactory.create(1);
        character.useCharacter(1, null);
        IslandTile islandTile = new IslandTile(null, true, null);
        Student studentToMove = ((MoveCharacter)character).getPawnByID(((MoveCharacter)character).getPawnIDs().iterator().next());
        ((MoveCharacter) character).useAbilityC1(studentToMove, islandTile);
        assertFalse(((MoveCharacter)character).getPawnIDs().contains(studentToMove.getID()));
        assertTrue(islandTile.getPawnIDs().contains(studentToMove.getID()));
        assertTrue(((MoveCharacter) character).getPawnIDs().size()==4);
    }

    @Test
    void useAbilityC7() {
    }

    @Test
    void useAbilityC10() {
    }

    @Test
    void useAbilityC11() { //TODO: when implemented
    }

    @Test
    void useAbilityC12() { //TODO: when implemented
    }
}