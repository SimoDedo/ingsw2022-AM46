package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.Utils.Enum.RequestParameters;
import it.polimi.ingsw.Utils.PlayerList;

import javax.management.modelmbean.RequiredModelMBean;
import java.util.*;

public class CharacterSet {

    /**
     * Characters instantiated for the game
     */
    private List<Character> characters;

    /**
     * Character factory used to create characters
     */
    private CharacterFactory characterFactory;

    public CharacterSet(Bag bag, PlayerList players){
        characters = new ArrayList<Character>();
        characterFactory = new CharacterFactory(bag, players);
        Set<Integer> randomIDs = selectRandomIDs();
        for (Integer integer : randomIDs){
            characters.add(characterFactory.create(integer));
        }
    }

    /**
     * Selects 3 random IDs between 1 and 12 with no duplicates
     * @return A set containing the 3 IDs
     */
    private Set<Integer> selectRandomIDs(){
        Set<Integer> IDs = new HashSet<Integer>();
        Random random = new Random(89);
        while (IDs.size() < 3){
            Integer next = random.nextInt(12) + 1;
            IDs.add(next);
        }
        return IDs;
    }

    private List<RequestParameters> useCharacter(Player player, int ID) throws IllegalStateException {
        for(Character character : characters){
            if(character.wasUsedThisTurn())
                throw  new IllegalStateException("One Character was already activated");
        }
        return null;
    }
}
