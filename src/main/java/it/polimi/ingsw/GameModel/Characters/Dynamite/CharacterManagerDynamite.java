package it.polimi.ingsw.GameModel.Characters.Dynamite;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.RequestParameters;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.*;

public class CharacterManagerDynamite { // merging lambdamanager and characterset

    private Archipelago archipelago;
    private Bag bag;
    private PlayerList playerList;
    private ProfessorSet professorSet;
    private List<CharacterDynamite> characters = new ArrayList<>(12);
    private CharacterDynamite currentCharacter;
    private CharacterFactoryDynamite charFactory = new CharacterFactoryDynamite();
    private ConsumerSetDynamite consumerSet;

    public CharacterManagerDynamite(Archipelago archipelago, Bag bag, PlayerList playerList, ProfessorSet professorSet) {
        this.archipelago = archipelago;
        this.bag = bag;
        this.playerList = playerList;
        this.professorSet = professorSet;
        consumerSet = new ConsumerSetDynamite(archipelago, bag, playerList, professorSet, characters);
        for (int index : selectRandomIndices()) {
            characters.add(index, charFactory.create(index+1));
        }
    }

    private Set<Integer> selectRandomIndices() {
        Set<Integer> indices = new HashSet<>();
        Random random = new Random(89);
        while (indices.size() < 3){
            Integer next = random.nextInt(12);
            indices.add(next);
        }
        return indices;
    }

    public List<RequestParameters> useCharacter(Player player, int ID) throws IllegalStateException {
        for(CharacterDynamite character : characters) {
            if (character.wasUsedThisTurn())
                throw new IllegalStateException("You can only use a character at a time");
        }
        currentCharacter = characters.get(ID - 1);
        return currentCharacter.useCharacter(player);
    }

    public void useAbility(List<Integer> parameterList) {
        int currentID = currentCharacter.getCharacterID();
        currentCharacter.useAbility(consumerSet.getConsumer(currentID), parameterList);
    }

}
