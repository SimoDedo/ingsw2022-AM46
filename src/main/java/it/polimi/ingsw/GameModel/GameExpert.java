package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyStandard;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyStandard;
import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Table;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.Characters.CharacterManager;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that extends Game. It is created instead of Game in an Expert match.
 * Its role is the same as that of game with the added functionality of the Expert match (Coins and Characters).
 */
public class GameExpert extends Game {

    private final CoinBag coinBag;
    private CharacterManager characterManager;

    /**
     * Constructor for GameExpert. Calls Game constructor.
     * Then creates a CoinBag and gives each player one coin. Finally, it creates 3 random Characters.
     * @param gameConfig the game configuration object, created by the GameFactory
     *
     */
    public GameExpert(GameConfig gameConfig) {
        super(gameConfig);
        coinBag = new CoinBag(20);
    }

    /**
     * Method for moving a student from the player's entrance to the dining room or to an island.
     * It checks if the student is actually inside the entrance, if the destination is a dining
     * room table, or if it is an island tile.
     * Awards coin to player when it has to.
     * @param nickname the nickname of the entrance's owner
     * @param studentID the ID of the entrance student to move
     * @param containerID the ID of the student container which will host the student
     */
    @Override
    public void moveStudentFromEntrance(String nickname, int studentID, int containerID) throws FullTableException {
        Player player = players.getByNickname(nickname);
        Student student = player.getStudentFromEntrance(studentID);
        Table potentialTable = player.getTable(student.getColor());
        if (potentialTable.getID() == containerID){
            student.getStudentContainer().removePawn(student);
            if(potentialTable.placeStudent(student))
                awardCoin(nickname);
        }
        else {
            archipelago.placeStudent(student, archipelago.getIslandTileByID(containerID)); // will have to throw exception
        }
        checkAndMoveProfessor(players, student.getColor());
    }

    /**
     * This method progresses the turn, updating currentPlayer.
     */
    @Override
    public void nextTurn() throws IllegalStateException{
        super.nextTurn();
        characterManager.resetActiveCharacter();
    }


    /**
     * Creates the characterManager (which creates the 3 characters). Called by the controller once all players are connected.
     */
    public void createCharacters(){
        characterManager = new CharacterManager(archipelago, bag, players, professorSet, coinBag);
    }

    /**
     * Awards each player with one coin. Called by the controller once all players are connected.
     */
    public void distributeInitialCoins(){
        for(Player player : players){
            try{
                coinBag.removeCoin();
                player.awardCoin();
            }catch ( ArithmeticException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Awards given player with a coin.
     * @param nickname the player to award the coin to.
     */
    private void awardCoin(String nickname){
        try{
            coinBag.removeCoin();
            players.getByNickname(nickname).awardCoin();
        }catch ( ArithmeticException e){e.printStackTrace();}
    }

    /**
     * Precedes a character's ability activation. Calls its delegate useCharacter inside the characterManager.
     * The method returns a list of parameters the user needs in order for its
     * ability to activate, which will be picked up by the controller.
     * @param nickname nickname of the player who activated this character
     * @param ID the ID of the character to use
     * @return a list of RequestParameters that will be needed by the game controller
     */
    public List<RequestParameter> useCharacter(String nickname, int ID) throws  IllegalStateException{
        return characterManager.useCharacter(players.getByNickname(nickname), ID);
    }

    /**
     * Method that executes the character's ability. It passes the list of parameters (in the form
     * of IDs of various pawns/board pieces) and the right Consumer according to the character's ID
     * to the currently active character.
     * @param parameterList the list of the consumer's parameters
     */
    public void useAbility(List<Integer> parameterList)
            throws NoSuchElementException, IllegalArgumentException, IllegalStateException,
            LastRoundException, GameOverException {
        characterManager.useAbility(parameterList);
    }

    /**
     * Method that performs operation each end of round (= when the last player has played his ActionPhase turn), such as:
     * Determining the winner if this is the last round to be played
     * Resetting the character used this round
     * Resetting the strategies
     */
    @Override
    public void endOfRoundOperations() throws GameOverException {
        super.endOfRoundOperations();
        characterManager.resetActiveCharacter();
        if(!(archipelago.getResolveStrategy() instanceof ResolveStrategyStandard))
            archipelago.setResolveStrategy(new ResolveStrategyStandard());
        if(!(archipelago.getMoveMotherNatureStrategy() instanceof  MoveMotherNatureStrategyStandard))
            archipelago.setMotherNatureStrategy(new MoveMotherNatureStrategyStandard());
        if(!(professorSet.getCheckAndMoveProfessorStrategy() instanceof CheckAndMoveProfessorStrategyStandard))
            professorSet.setCheckAndMoveProfessorStrategy(new CheckAndMoveProfessorStrategyStandard());
        return;
    }

    //region State observer methods

        //region Characters
        /**
         * method to observe number of coins of a given player.
         * @param nickname the player to check
         * @return the number of coins of the given player
         */
        public int getCoins(String nickname){
            return players.getByNickname(nickname).getCoins();
        }

        /**
         * Method to observe which characters were created for this game.
         * @return a list of the created character IDs.
         */
        public List<Integer> getDrawnCharacterIDs(){
            List<Integer> charactersIDs = new ArrayList<>();
            for (int i = 1; i < 13; i++)
                if (characterManager.getCharacterByID(i) != null)
                    charactersIDs.add(i);
            return  charactersIDs;
        }

        /**
         * Getter for the ActiveCharacter ID.
         * @return the ActiveCharacter ID.
         */
        public int getActiveCharacterID(){
            return characterManager.getActiveCharacterID();
        }

        /**
         * Return the maximum number of times the ability of the active character can be used.
         * @return the maximum number of times the ability of the active character can be used.
         */
        public int getActiveCharacterMaxUses(){
            return characterManager.getActiveCharacterMaxUses();
        }

        /**
         * Returns the number of times the ability of the active character can still be used.
         * @return the number of times the ability of the active character can still be used.
         */
        public int getActiveCharacterUsesLeft(){
            return characterManager.getActiveCharacterUsesLeft();
        }

        //endregion

    //endregion
}
