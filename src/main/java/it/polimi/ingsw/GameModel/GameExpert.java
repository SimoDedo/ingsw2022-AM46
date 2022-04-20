package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyStandard;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyStandard;
import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Characters.CharacterManager;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.List;

/**
 * Class that extends Game. It is created instead of Game in an Expert match.
 * Its role is the same as that of game with the added functionality of the Expert match (Coins and Characters).
 */
public class GameExpert extends Game {

    private CoinBag coinBag;
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
     * Creates the characterManager (which creates the 3 characters). Called by the controller once all players are connected.
     */
    public void createCharacters(){
        characterManager = new CharacterManager(archipelago, bag, players, professorSet);
    }

    /**
     * Awards each player with one coin. Called by the controller once all players are connected.
     */
    public void distributeInitialCoins(){
        for(Player player : players){
            coinBag.removeCoin();
            player.awardCoin();
        }
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
    public void useAbility(List<Integer> parameterList) {
        characterManager.useAbility(parameterList);
    }

    /**
     * Method that performs operation each end of round (= when the last player has played his ActionPhase turn), such as:
     * Determining the winner if this is the last round to be played
     * Resetting the character used this round
     * Resetting the strategies
     * Changing the Phase (????????)
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
}
