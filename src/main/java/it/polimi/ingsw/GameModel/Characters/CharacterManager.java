package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that stores and manages Characters, directing their activation and giving them the Consumer
 * objects which act as their abilities.
 */
public class CharacterManager  implements Serializable {

    private final List<AbstractCharacter> characters = new ArrayList<>(12);
    private AbstractCharacter currentCharacter;
    private List<RequestParameter> currentRequestParameters = new ArrayList<>();
    final transient private CharacterFactory charFactory = new CharacterFactory();
    final transient private ConsumerSet consumerSet;

    /**
     * Constructor for the CharacterManager. Selects three random characters to create.
     * @param archipelago the game's archipelago
     * @param bag the game's student bag
     * @param playerList the game's player list
     * @param professorSet the game's professor set
     */
    public CharacterManager(Archipelago archipelago, Bag bag, PlayerList playerList, ProfessorSet professorSet, CoinBag coinbag) {
        consumerSet = new ConsumerSet(archipelago, bag, playerList, professorSet, characters, coinbag);
        List<Integer> IDs = selectRandomCharIDs();
        for (int i = 0; i < 12; i++) {
            characters.add(null);
        }
        for (int charID : IDs) createCharacter(charID, bag);
    }

    /**
     * Helper function that selects three different numbers from 1 to 12. Used to create three
     * random characters during setup of the game.
     * @return a list containing the three integers
     */
    private List<Integer> selectRandomCharIDs() {
        List<Integer> IDs = new ArrayList<>();
        Random random = new Random();
        while (IDs.size() < 3){
            Integer next = random.nextInt(12) + 1;
            if(! IDs.contains(next))
                IDs.add(next);
        }
        return IDs;
    }

    /**
     * Method that initializes a character with the given ID and puts it inside the character list.
     * @param ID the ID of the character to create
     */
    public void createCharacter(int ID, Bag bag) { // is only public for testing purposes
        characters.set(ID - 1, charFactory.create(ID, bag));
    }

    /**
     * Precedes a character's ability activation. Calls its delegate useCharacter inside the character
     * with the given ID. The method returns a list of parameters the user needs in order for its
     * ability to activate, which will be picked up by the controller.
     * @param player the player who activated this character
     * @param bag The coin bag to which coins used will be returned
     * @param ID the ID of the character to use
     * @return a list of RequestParameters that will be needed by the game controller
     */
    public List<RequestParameter> useCharacter(Player player, CoinBag bag, int ID) throws IllegalStateException, IllegalArgumentException {
        for(Character character : characters) {
            if (character != null && character.wasUsedThisTurn())
                throw new IllegalStateException("You can only use a character at a time");
        }
        currentRequestParameters = characters.get(ID - 1).useCharacter(player, bag); //This could throw exceptions
        currentCharacter = characters.get(ID - 1);
        return currentRequestParameters;
    }

    /**
     * Method that executes the character's ability. It passes the list of parameters (in the form
     * of IDs of various pawns/board pieces) and the right Consumer according to the character's ID
     * to the currently active character.
     * @param parameterList the list of the consumer's parameters
     */
    public void useAbility(List<Integer> parameterList)
            throws NoSuchElementException, IllegalArgumentException, IllegalStateException,
            FullTableException, LastRoundException, GameOverException {
        if(currentCharacter == null)
            throw new IllegalStateException("No character was activated!");
        else if(currentCharacter.getUsesLeft() <= 0)
            throw new IllegalStateException("Character has no more uses lef!");

        int currentID = currentCharacter.getCharacterID();
        currentCharacter.useAbility(consumerSet.getConsumer(currentID), parameterList);
    }

    /**
     * Getter for the Character with the given ID.
     * @param ID the ID of the Character to find
     * @return the Character with the given ID
     */
    public AbstractCharacter getCharacterByID(int ID) {
        return characters.get(ID-1);
    }

    /**
     * Getter for the ActiveCharacter ID.
     * @return the ActiveCharacter ID, -1 if no character is active
     */
    public int getActiveCharacterID() {
        return currentCharacter == null ? -1 : currentCharacter.getCharacterID();
    }

    /**
     * Return the maximum number of times the ability of the active character can be used.
     * @return the maximum number of times the ability of the active character can be used.
     */
    public int getActiveCharacterMaxUses(){
        return currentCharacter == null ? 0 : currentCharacter.getMaxUses();
    }

    /**
     * Returns the number of times the ability of the active character can still be used.
     * @return the number of times the ability of the active character can still be used.
     */
    public int getActiveCharacterUsesLeft(){
        return currentCharacter == null ? 0 : currentCharacter.getUsesLeft();
    }

    /**
     * Resets the character that was used this round, if there was any
     */
    public void resetActiveCharacter(){
        currentRequestParameters.clear();
        for(Character character : characters) {
            if (character != null && character.wasUsedThisTurn())
                character.resetUseState();
        }
        currentCharacter = null;
    }

    /**
     * @return a list of IDs of the 3 characters that were randomly chosen for this game
     */
    public List<Integer> getCurrentCharacterIDs() {
        return characters.stream().filter(Objects::nonNull).map(AbstractCharacter::getCharacterID).collect(Collectors.toList());
    }

    /**
     * Getter for the students contained on a given character.
     * @param ID the ID of the character to inspect
     * @return a hash map containing the ID of the students as key and their color as value.
     * If no students are contained, the map will be empty
     */
    public HashMap<Integer, Color> getCharacterStudents(int ID){
        if(characters.get(ID - 1) instanceof StudentMoverCharacter)
            return ((StudentMoverCharacter) characters.get(ID - 1)).getStudentIDsAndColor();
        else
            return new HashMap<>();
    }

    /**
     * Getter for the current cost of the character.
     * @param ID the ID of the character requested
     * @return the cost
     */
    public int getCharacterCost(int ID){
        return characters.get(ID -1) != null ? characters.get(ID - 1).getCost() : 0;
    }

     /**
     * Returns true if the given character is overcharged.
     * @param ID the character to check
     * @return true if the given character is overcharged.
     */
     public boolean getCharacterOvercharge(int ID){
         return ! characters.get(ID -1).isFirstUse();
     }

    /**
     * Getter for the number of entry tiles left on the character
     * @return the number of entry tiles left on the character
     */
    public int getNoEntryTilesCharacter(int ID) {
        return ID == 5 ? ((NoEntryCharacter)characters.get(ID - 1)).getNoEntryTiles() : 0;
    }

    /**
     * Gets the current requested parameters for the active character
     * @return the current requested parameters for the active character
     */
    public List<RequestParameter> getCurrentRequestParameters() {
        return currentRequestParameters;
    }
}
