package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CharacterFactory on some outputs
 */
class CharacterFactoryTest {

    /**
     * Tests creation of character with ID 1
     * @throws FullTeamException Not tested here
     */
    @Test
    void create1() throws FullTeamException {
        CharacterFactory characterFactory = new CharacterFactory(new Bag(), new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.getCharacterID() == 1 && character.getCost() == 1 && character instanceof MoveCharacter);
    }

    /**
     * Tests creation of character with ID 2
     * @throws FullTeamException Not tested here
     */
    @Test
    void create2() throws FullTeamException { //FIXME: when implemented
        CharacterFactory characterFactory = new CharacterFactory(new Bag(), new PlayerList());
        Character character = characterFactory.create(1);
        assertTrue(character.getCharacterID() == 1 && character.getCost() == 1 && character instanceof MoveCharacter);
    }

    /**
     * Tests creation of character with ID 3
     * @throws FullTeamException Not tested here
     */
    @Test
    void create3() throws FullTeamException {
        CharacterFactory characterFactory = new CharacterFactory(new Bag(), new PlayerList());
        Character character = characterFactory.create(3);
        assertTrue(character.getCharacterID() == 3 && character.getCost() == 3 && character instanceof ResolveStrategyCharacter);
    }

    /**
     * Tests creation of character with ID 4
     * @throws FullTeamException Not tested here
     */
    @Test
    void create4() throws FullTeamException {
        CharacterFactory characterFactory = new CharacterFactory(new Bag(), new PlayerList());
        Character character = characterFactory.create(4);
        assertTrue(character.getCharacterID() == 4 && character.getCost() == 1 && character instanceof MoveMotherNatureCharacter);
    }

    /**
     * Tests creation of character with ID 5
     * @throws FullTeamException Not tested here
     */
    @Test
    void create5() throws FullTeamException {
        CharacterFactory characterFactory = new CharacterFactory(new Bag(), new PlayerList());
        Character character = characterFactory.create(5);
        assertTrue(character.getCharacterID() == 5 && character.getCost() == 2 && character instanceof NoEntryTileCharacter);
    }
}