package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.RequestParameters;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.*;

// merging lambdamanager and characterset
public class CharacterManager {

    private Archipelago archipelago;
    private Bag bag;
    private PlayerList playerList;
    private ProfessorSet professorSet;
    private List<AbstractCharacter> characters = new ArrayList<>(12);
    private AbstractCharacter currentCharacter;
    private CharacterFactory charFactory = new CharacterFactory();
    private ConsumerSet consumerSet;

    public CharacterManager(Archipelago archipelago, Bag bag, PlayerList playerList, ProfessorSet professorSet) {
        this.archipelago = archipelago;
        this.bag = bag;
        this.playerList = playerList;
        this.professorSet = professorSet;
        consumerSet = new ConsumerSet(archipelago, bag, playerList, professorSet, characters);
        List<Integer> IDs = selectRandomCharIDs();
        for (int i = 0; i < 12; i++) {
            characters.add(null);
        }
        for (int charID : IDs) createCharacter(charID);
    }

    private List<Integer> selectRandomCharIDs() {
        List<Integer> indices = new ArrayList<>();
        Random random = new Random(89);
        while (indices.size() < 3){
            Integer next = random.nextInt(12) + 1;
            indices.add(next);
        }
        return indices;
    }

    public void createCharacter(int ID) { // is only public for testing purposes
        characters.set(ID - 1, charFactory.create(ID));
    }

    public List<RequestParameters> useCharacter(Player player, int ID) throws IllegalStateException {
        for(Character character : characters) {
            if (character != null && character.wasUsedThisTurn())
                throw new IllegalStateException("You can only use a character at a time");
        }
        currentCharacter = characters.get(ID - 1);
        return currentCharacter.useCharacter(player);
    }

    public void useAbility(List<Integer> parameterList) {
        int currentID = currentCharacter.getCharacterID();
        currentCharacter.useAbility(consumerSet.getConsumer(currentID), parameterList);
    }

    public AbstractCharacter getCharacterByID(int ID) {
        return characters.get(ID-1);
    }
}
