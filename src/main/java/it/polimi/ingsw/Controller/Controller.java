package it.polimi.ingsw.Controller;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.GameExpert;
import it.polimi.ingsw.GameModel.GameFactory;
import it.polimi.ingsw.GameModel.ObservableByClient;
import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Error.IllegalActionError;
import it.polimi.ingsw.Network.Message.Error.IllegalSelectionError;
import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Update.Update;
import it.polimi.ingsw.Network.Message.UserAction.*;
import it.polimi.ingsw.Network.Server.MatchServer;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that models the controller of the MVC pattern.
 */
public class Controller {

    private final MatchServer server;

    private final TurnController turnController;

    /**
     * The factory used to create the game.
     */
    private final GameFactory gameFactory;

    /**
     * The game which the controller controls.
     */
    private Game game;

    /**
     * List of players who connected to the game controlled by this controller
     */
    private final List<String> players;

    private int numOfPlayers;
    private GameMode gameMode;

    /**
     * Keeps track of number of players who selected TowerColor and Wizard
     */
    private int playersReady;

    /**
     * HashMap that contains nickname of a user and the expectedUserAction. The expected user action represent
     * the user must take in order for the game state to evolve
     */
    private final HashMap<String, UserActionType> expectedUserAction;

    /**
     * True when game settings have been selected and thus game is created
     */
    private boolean initialized;

    /**
     * True when the first planning phase has started
     */
    private boolean gameStarted;

    /**
     * True whenever the round being played is the last
     */
    private boolean isLastRound;

    /**
     * Constructor for the controller. Creates a controller in its starting state, waiting for someone to login.
     */
    public Controller(MatchServer server){
        this.server = server;
        gameStarted = false;
        isLastRound = false;
        players = new ArrayList<>();
        numOfPlayers = 0;
        playersReady = 0;
        turnController = new TurnController();
        gameFactory = new GameFactory();
        expectedUserAction = new HashMap<>();
    }

    /**
     * Constructor for the controller used in testing. Creates a controller in its starting state,
     * waiting for someone to login. The server is set to null and messages won't actually be sent over the network.
     */
    public Controller(){
        this.server = null;
        gameStarted = false;
        isLastRound = false;
        players = new ArrayList<>();
        numOfPlayers = 0;
        playersReady = 0;
        turnController = new TurnController();
        gameFactory = new GameFactory();
        expectedUserAction = new HashMap<>();
    }


    //region Network
    /**
     * Main method though which the server interacts with the controller.
     * The server receives messages (UserAction) which are then forwarded to the controller though this method.
     * The method parses the type of userAction and acts accordingly.
     * If the UserAction pertains to a Login or a Character usage, it gets handled separately.
     * Otherwise, it is first checked if the request can be accepted and then examined based on its type.
     * After being parsed, one or more private controller methods are called. These methods change the game state
     * along with sending an update or an error to the user.
     * @param userAction the action taken by the user.
     */
    public void receiveUserAction(UserAction userAction){

        String nickname = userAction.getSender();

        if (userAction.getUserActionType() == UserActionType.LOGIN){
            this.loginHandle(((LoginUserAction)userAction).getNickname());
        }

        else if(userAction.getUserActionType() == UserActionType.USE_CHARACTER ||
                userAction.getUserActionType() == UserActionType.USE_ABILITY){
            this.characterRequestHandle(userAction);
        }

        else {
            if (gameStarted && !turnController.getCurrentPlayer().equals(nickname)) {
                //If game started, then only a specific user can take action. Before it starts, anyone can send their selection to make process faster
                sendErrorToUser(nickname, new IllegalActionError("You can only act during your turn."));
            }
            else if (expectedUserAction.get(nickname) == null ||
                    !expectedUserAction.get(nickname).equals(userAction.getUserActionType())) {
                //User is always expected to send a certain user action to progress game.
                //The only exception are Login and Character actions (accounted and treated separately above)
                System.out.println(userAction.getUserActionType());
                sendErrorToUser(nickname, new IllegalActionError("Action taken is not a valid action. A " +
                        expectedUserAction.get(nickname) + " is expected"));
            }
            else {
                switch (userAction.getUserActionType()) {
                    case GAME_SETTINGS -> {
                        int n = ((GameSettingsUserAction) userAction).getNumOfPlayers();
                        GameMode gameMode = ((GameSettingsUserAction) userAction).getGameMode();
                        selectGameSettings(n, gameMode);
                        initialized = true;
                    }
                    case TOWER_COLOR -> {
                        TowerColor towerColor = ((TowerColorUserAction) userAction).getTowerColor();
                        addPlayer(nickname, towerColor);
                    }
                    case WIZARD -> {
                        WizardType wizardType = ((WizardUserAction) userAction).getWizardType();
                        chooseWizard(nickname ,wizardType);
                        if(playersReady == numOfPlayers)
                            startGame();
                    }
                    case PLAY_ASSISTANT -> {
                        int assistantID = ((PlayAssistantUserAction) userAction).getAssistantID();
                        playAssistant(nickname, assistantID);
                    }
                    case MOVE_STUDENT -> {
                        int studentID = ((MoveStudentUserAction) userAction).getStudentID();
                        int islandOrTableID = ((MoveStudentUserAction) userAction).getIslandOrTableID();
                        moveStudentFromEntrance(nickname, studentID, islandOrTableID);
                    }
                    case MOVE_MOTHER_NATURE -> {
                        int islandID = ((MoveMotherNatureUserAction) userAction).getIslandID();
                        moveMotherNature(nickname, islandID);
                    }
                    case TAKE_FROM_CLOUD -> {
                        int cloudID = ((TakeFromCloudUserAction) userAction).getCloudID();
                        takeFromCloud(nickname, cloudID);
                    }
                }
            }
        }
    }

    /**
     * Handles login of a given player. It is assumed that the received nickname is unique.
     * If the player who connects is the first to do so, starts by expecting game mode parameters.
     * If it isn't, adds the player only if the game parameters have been chosen and the game isn't already full,
     * otherwise returns a login error. The choice of a tower color is expected of them.
     * In either case, the first update from the controller is sent here.
     * @param nickname nickname of the player trying to join the game. Assumed unique.
     */
    public void loginHandle(String nickname){ //Check on username uniqueness done by SERVER (needs to be unique between ALL games and controllers. Assumed here unique.)
        if(players.size() == 0){ //If first to login
            players.add(nickname);

            expectedUserAction.put(nickname, UserActionType.GAME_SETTINGS);
            sendUpdateToAllUsers(new Update(game, null, null,nickname, UserActionType.GAME_SETTINGS,
                    "You are the first player to connect, choose the settings for this game"));
        }
        else{
            if(numOfPlayers != 0 && gameMode != null){ //If first player has chosen game settings
                if(players.size() < numOfPlayers){ //If there is still space in the game
                    players.add(nickname);
                    expectedUserAction.put(nickname, UserActionType.TOWER_COLOR);
                    sendUpdateToAllUsers(new Update(game, null, null, nickname, UserActionType.TOWER_COLOR,
                            "Choose your tower color"));
                }
                else { //If there is no more space
                    sendErrorToUser(nickname, new LoginError("Game ha no more spot available!"));
                }
            }
            else{ //If game settings have not been chosen yet
                sendErrorToUser(nickname, new LoginError("Lobby has not yet chosen game settings."));
            }
        }
    }

    /**
     * Handles UseCharacter and UseAbility UserActions.
     * Both are denied if the game isn't an expert game, if the game hasn't started yet, if the player sending
     * is not the current player or if it isn't the action phase.
     * Additionally, UseAbility UserActions get denied if no character had previously been activated and if
     * no uses are left.
     * If all these conditions are met, it tries to activate the character or its ability, which will either
     * trigger the sending of an update or an error
     * @param userAction the action taken by the user.
     */
    private void characterRequestHandle(UserAction userAction){
        String nickname = userAction.getSender();

        if(gameMode != GameMode.EXPERT){
            sendErrorToUser(nickname, new IllegalActionError("Can't activate character in Normal game mode!"));
        }
        else  if(!gameStarted){
            sendErrorToUser(nickname, new IllegalActionError("Game hasn't started yet, can't play character!"));
        }
        else if(!turnController.getCurrentPlayer().equals(nickname)){
            sendErrorToUser(nickname, new IllegalActionError("Can't play a character during another players turn!"));
        }
        else if(turnController.getCurrentPhase() != Phase.ACTION){
            sendErrorToUser(nickname, new IllegalActionError("Can't play a character during Planning phase!"));
        }
        else{
            switch (userAction.getUserActionType()){
                case USE_CHARACTER -> {
                    int characterID = ((UseCharacterUserAction) userAction).getCharacterID();
                    useCharacter(nickname, characterID);
                }
                case USE_ABILITY -> {
                    List<Integer> requestParameters = ((UseAbilityUserAction) userAction).getRequestedParameters();
                    useAbility(nickname, requestParameters);
                }
            }
        }
    }

    /**
     * Method to send an update to all users. It asks the (match) server to do this operation.
     * If no server was set on initialization, the update is instead printed (used for testing).
     * @param update the info to send.
     */
    private void sendUpdateToAllUsers(Update update){
        if(server != null)
            server.sendAll(update);
        else
            System.out.println(update);
    }

    /**
     * Method to send errors to a specific user.
     * @param error error to send
     * @param nickname the user to send the error to
     */
    public void sendErrorToUser(String nickname, Error error){
        if(server != null)
            server.sendMessage(nickname, error);
        else
            System.out.println(error);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isFull() {
        return numOfPlayers != 0 && numOfPlayers == players.size();
    }

    //endregion

    //region Game handle

    /**
     * Sets the game settings. Then, sends an update. The user is then expected to choose a tower color.
     * @param numOfPlayers the number of players of the game.
     * @param gameMode the game mode of the game.
     */
    private void selectGameSettings(int numOfPlayers, GameMode gameMode){
        this.numOfPlayers = numOfPlayers;
        turnController.setNumOfPlayers(numOfPlayers);
        this.gameMode = gameMode;

        createGame();

        expectedUserAction.put(players.get(0), UserActionType.TOWER_COLOR);
        sendUpdateToAllUsers(new Update(game, players.get(0), UserActionType.GAME_SETTINGS,
                players.get(0), UserActionType.TOWER_COLOR, "Choose your tower color"));
    }

    /**
     * Creates the game.
     */
    private void createGame(){
        game = gameFactory.create(numOfPlayers, gameMode);
        turnController.setGame(game);
    }

    /**
     * Adds player to the game with the tower color chosen. Sends an error if the team of that TowerColor is already
     * full. Otherwise, sends an update. User is then expected to choose a wizard.
     * @param nickname the player who chose.
     * @param towerColor the tower color chosen.
     */
    private void addPlayer(String nickname, TowerColor towerColor){
        try{
            game.createPlayer(nickname, towerColor);
        }
        catch (IllegalArgumentException e){
            sendErrorToUser(nickname, new IllegalSelectionError( e.getLocalizedMessage()));
            return;
        }

        expectedUserAction.put(nickname, UserActionType.WIZARD);
        sendUpdateToAllUsers(new Update(game, nickname, UserActionType.TOWER_COLOR, nickname, UserActionType.WIZARD,
                "Choose your wizard"));

    }

    /**
     * Assigns wizard to a player. Sends an error if wizard was already chosen. Otherwise, sends an update.
     * The player who chose is not expected to take any action while waiting for the game to start.
     * @param nickname the player who chose.
     * @param wizardType the wizard chosen.
     */
    private void chooseWizard(String nickname,WizardType wizardType){
        try {
            game.assignWizard(nickname, wizardType);
        }catch (IllegalArgumentException e){
            sendErrorToUser(nickname,  new IllegalSelectionError(e.getLocalizedMessage()));
            return;
        }

        expectedUserAction.remove(nickname); //Not expecting any action from this player now, he should be waiting for his first turn
        playersReady++;
        sendUpdateToAllUsers(new Update(game, nickname, UserActionType.WIZARD,
                nickname, UserActionType.WAIT_GAME_START, "Wait for all players to have chosen!"));
    }

    /**
     * Starts the actual game. Sends the first update expecting a PlayAssistantUserAction.
     */
    private void startGame(){
        turnController.startGame();
        gameStarted = true;

        if(gameMode == GameMode.EXPERT){
            ((GameExpert) game).distributeInitialCoins();
            ((GameExpert) game).createCharacters();
        }

        game.refillClouds();


        //At start of game, first player (randomly drawn by model) is expected to play an assistant
        expectedUserAction.put(turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT);
        sendUpdateToAllUsers(new Update(game, null, UserActionType.WAIT_GAME_START,
                turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT, "Choose assistant to play"));
    }

    /**
     * Plays the assistant chosen.
     * Sends an error if no assistant with such ID exists or if that assistant can't be played.
     * If no error occurs, sends an update.
     * The turn is progressed and either the next player is expected to play an assistant, or the phase is switched
     * and the first player is expected to move a student.
     * If this is the last turn to be played, performs last round operations.
     * @param nickname the player who played the card.
     * @param assistantID the ID of the assistant to play.
     */
    private void playAssistant(String nickname, int assistantID){
        try { //Tries to play assistant selected
            game.playAssistant(nickname, assistantID);
        }catch (IllegalArgumentException | NoSuchElementException | LastRoundException e){
            if( e instanceof LastRoundException){
                this.lastRoundOperations();
            }
            else{ //if(e instanceof NoSuchElementException || e instanceof IllegalArgumentException)
                sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
                return;
            }
        }

        expectedUserAction.clear();
        try{ //If another player has to play an assistant, it will expect them to play it
            turnController.nextTurn();
            expectedUserAction.put(turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.PLAY_ASSISTANT,
                    turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT, "Choose assistant to play"));
        }catch (IllegalStateException phaseDone){
            //If all players have played their assistant, it will progress the phase and ask for next request
            turnController.nextPhase(); //Will compute the Action order, request made to player who played lowest card
            expectedUserAction.put(turnController.getCurrentPlayer(), UserActionType.MOVE_STUDENT);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.PLAY_ASSISTANT,
                    turnController.getCurrentPlayer(), UserActionType.MOVE_STUDENT, "Move student from entrance"));
        }
    }

    /**
     * Moves the student from the entrance to the selected destination.
     * Sends an error if the action selected is illegal.
     * If no error occurs, sends an update.
     * Either the player has more students to move (and is expected to do so), or is expected to move mother nature.
     * @param nickname the player who moved the student
     * @param studentID the ID of the student to move
     * @param destinationID the ID of the destination (table or island)
     */
    private void moveStudentFromEntrance(String nickname, int studentID, int destinationID){
        try {
            game.moveStudentFromEntrance(nickname, studentID, destinationID);
        }catch (FullTableException e){
            sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
            return;
        }
        turnController.moveStudent();

        if(turnController.studentMoved() < turnController.getStudentsToMove()){
            //If more students to move, expects another student moved
            expectedUserAction.put(nickname, UserActionType.MOVE_STUDENT);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.MOVE_STUDENT,
                    nickname, UserActionType.MOVE_STUDENT, "Move student from entrance"));
        }
        else {//If no more students to move, expects user to move mother nature
            expectedUserAction.put(nickname, UserActionType.MOVE_MOTHER_NATURE);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.MOVE_STUDENT,
                    nickname, UserActionType.MOVE_MOTHER_NATURE, "Move mother nature"));
        }
    }

    /**
     * Moves mother nature to the selected island.
     * Sends an error if the chosen island is beyond player's reach.
     * If the game ends, end game operations are performed.
     * Otherwise, an update is sent.
     * The user is expected to choose a cloud, unless this is the last turn, in which case end of round operations
     * are performed.
     * @param nickname the player who move mother nature.
     * @param islandID the destination island of mother nature
     */
    private void moveMotherNature(String nickname, int islandID){
        try { //If move is valid, mother nature is moved (and all other consequent operations are done by the model),
            // then next request is sent to choose a cloud to refill entrance
            game.moveMotherNature(nickname, islandID);
        }
        catch (IllegalArgumentException | GameOverException e){ //If it isn't valid error and action must be retaken. If game is over, message
            if(e instanceof  GameOverException){
                gameOverOperations();
            }
            else{
                sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
            }
            return;
        }

        if(!isLastRound){
            expectedUserAction.put(nickname, UserActionType.TAKE_FROM_CLOUD);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.MOVE_MOTHER_NATURE,
                    nickname, UserActionType.TAKE_FROM_CLOUD, "Take from cloud"));
        }
        else{
            //If it's the last round, clouds are disabled and cloud selection is skipped (since useless, it's the last round no one cares)
            //CHECKME: always to disable clouds?
            expectedUserAction.clear();
            endOfRoundOperations(nickname, UserActionType.MOVE_MOTHER_NATURE); //Since its last round, WinnerInfo will be sent.
        }
    }

    /**
     * Takes the contents of chosen cloud and puts them in the user's entrance.
     * Sends an error if the cloud was already taken.
     * The turn is progressed and if there is a next player, it is expected to move a student and an update is sent.
     * Otherwise, end of round operations are performed.
     * @param nickname the player who took from the cloud.
     * @param cloudID the cloud selected.
     */
    private void takeFromCloud(String nickname, int cloudID){
        try {
            game.takeFromCloud(nickname, cloudID);
        }
        catch (NoSuchElementException e){
            sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
            return;
            //TODO: other exceptions should be thrown and handled
        }

        try { //If there is a next player that has to play its Action phase, sends request to him
            turnController.nextTurn();
            expectedUserAction.clear();
            expectedUserAction.put(turnController.getCurrentPlayer(), UserActionType.MOVE_STUDENT);
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.TAKE_FROM_CLOUD,
                    turnController.getCurrentPlayer(), UserActionType.MOVE_STUDENT, "Move student from entrance"));
        }
        catch (IllegalStateException phaseDone){ //If instead all players have played, phase is switched
            endOfRoundOperations(nickname, UserActionType.TAKE_FROM_CLOUD);
        }
    }

    /**
     * Uses (in the sense that it gets paid for) a character.
     * Sends an error if the character can't be used.
     * If no error occurs, sends an update.
     * If no selection is required to the user, the ability is instead immediately activated.
     * @param nickname the player who used the character.
     * @param characterID the ID of the character used.
     */
    private void useCharacter(String nickname, int characterID){
        List<RequestParameter> requestParameters;
        try{
            requestParameters = ((GameExpert) game).useCharacter(nickname, characterID);
        }
        catch (IllegalStateException | IllegalArgumentException e){
            sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
            return;
        }

        if(requestParameters.size() == 0 && ((GameExpert) game).getActiveCharacterMaxUses() == 1){
            //If no selection is required and character can only be used once, ability is instantly activated.
            // The second condition is always true, but if we were to add new characters that request no parameters
            // but can be used more than once, they shouldn't be used automatically
            useAbility(nickname, new ArrayList<>());
        }
        else {
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.USE_CHARACTER,
                    turnController.getCurrentPlayer(), UserActionType.USE_ABILITY, "Can now use character ability"));
        }
    }

    /**
     * Uses the ability of the active character.
     * Sends an error if any of the chosen parameters are illegal.
     * If no error occurs, an update is sent.
     * If this is the last turn to be played, performs last round operations.
     * @param nickname the player who used the ability.
     * @param parameters the parameters to use the ability.
     */
    private void useAbility(String nickname, List<Integer> parameters){
        try {
            ((GameExpert) game).useAbility(parameters);
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException
                | LastRoundException | GameOverException e){
            if(e instanceof  GameOverException){
                gameOverOperations();
                return;
            }
            else if (e instanceof  LastRoundException){
                this.lastRoundOperations();
            }
            else{
                sendErrorToUser(nickname, new IllegalSelectionError(e.getLocalizedMessage()));
            }
            //TODO: other exceptions should be thrown and handled. done? to check
        }
        if(parameters.size() == 0 && ((GameExpert) game).getActiveCharacterMaxUses() == 1){
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.USE_CHARACTER,
                    turnController.getCurrentPlayer(), UserActionType.USE_ABILITY, "Ability used automatically"));
        }
        else {
            sendUpdateToAllUsers(new Update(game, nickname, UserActionType.USE_ABILITY,
                    turnController.getCurrentPlayer(), UserActionType.USE_ABILITY, "Ability used, may now use another"));
        }
    }

    //Following methods are used by those above, not directly called in parsing

    /**
     * Performs actions at end of round.
     * If the game has ended, it performs end of round operations.
     * Otherwise, progresses to next phase and refills the clouds, then sends an update.
     * The first user of the planning phase is expected to play an assistant.
     * If this is the last turn to be played, performs last round operations.
     * @param lastPlayerToAct the player who had taken the last user action.
     * @param lastUserActionTaken the user action that ended the round.
     */
    private void endOfRoundOperations(String lastPlayerToAct, UserActionType lastUserActionTaken){
        try { //perform end of round operations
            game.endOfRoundOperations();
        }catch (GameOverException e){
            gameOverOperations();
            return;
        }

        turnController.nextPhase(); //Will compute the Planning order

        try {
            game.refillClouds();
        }catch (LastRoundException lastRound){
            this.lastRoundOperations();
        }
        expectedUserAction.put(turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT);
        sendUpdateToAllUsers(new Update(game, lastPlayerToAct, lastUserActionTaken,
                turnController.getCurrentPlayer(), UserActionType.PLAY_ASSISTANT, "Choose your assistant"));
    }

    /**
     * Operations done when the current round is set to be the last.
     * To be called whenever a LastRoundException is caught.
     * Sets the last round on the game and disables its clouds, then sets lastRound as true on the controller.
     */
    private void lastRoundOperations(){
        game.setLastRound();
        game.disableClouds(); //CHECKME: always to disable?
        this.isLastRound = true;
    }

    /**
     * Sends an update and clears the expected user actions;
     * no player is expected to play in this game now that it ended.
     */
    private void gameOverOperations(){
        expectedUserAction.clear();
        TowerColor winner = game.determineWinner();
        sendUpdateToAllUsers(new Update(game, null, null,
                null, UserActionType.END_GAME, "Winner has been determined, end game"));
    }

    //endregion

    //region testing methods

    /**
     * Protected method that allows unit tests to observe the action that is expected.
     * It is useful to check the correct evolution of the controller state.
     * @return a hash map containing a nickname as key and the expected user action as value.
     */
    protected HashMap<String, UserActionType> getExpectedUserAction() {
        return expectedUserAction;
    }

    /**
     * Protected method that allows tests to observe the game in order to observe its state and query the controller.
     * @return an ObservableByClient, which is the game controlled.
     */
    protected ObservableByClient getGame(){
        return game;
    }
    //endregion
}
