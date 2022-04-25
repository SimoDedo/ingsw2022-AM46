package it.polimi.ingsw.Controller;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.GameExpert;
import it.polimi.ingsw.GameModel.GameFactory;
import it.polimi.ingsw.Network.Message.Error.Error;
import it.polimi.ingsw.Network.Message.Error.IllegalActionError;
import it.polimi.ingsw.Network.Message.Error.LoginError;
import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Network.Message.Request.CharacterRequest;
import it.polimi.ingsw.Network.Message.Request.EndGameRequest;
import it.polimi.ingsw.Network.Message.Request.Request;
import it.polimi.ingsw.Network.Message.UserAction.*;
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
     * HashMap that contains nickname of a user and the last request made to that user.
     * Aside from using a character, a user should always respond with the UserAction requested by
     * the last request stored here.
     */
    private final HashMap<String, Request> lastRequestMade;

    /**
     * Stores the current character ability request. It contains all parameters requested to the user in order
     * to activate the ability, along with the uses left for that ability.
     * Null if no player has been activated this round.
     */
    private CharacterRequest characterAbilityRequest;

    private boolean gameStarted;
    private boolean isLastRound;

    /**
     * Constructor for the controller. Creates a controller in its starting state, waiting for someone to login.
     */
    public Controller(){
        gameStarted = false;
        isLastRound = false;
        players = new ArrayList<>();
        numOfPlayers = 0;
        playersReady = 0;
        turnController = new TurnController();
        lastRequestMade = new HashMap<>();
        gameFactory = new GameFactory();
    }


    //region Network
    /**
     * Main method though which the server interacts with the controller.
     * The server receives messages (UserAction) which are then forwarded to the controller though this method.
     * The method parses the type of userAction and acts accordingly.
     * If the UserAction pertains to a Login or a Character usage, it gets handled separately.
     * Otherwise, it is first checked if the request can be accepted and then examined based on its type.
     * After being parsed, one or more private controller methods are called. These methods change the game state and
     * return the next request to send the user or an error and sends it to the user.
     * @param userAction the action taken by the user.
     */
    public void receiveUserAction(UserAction userAction){
        Message messageToSend = null;
        String nickname = userAction.getSender();

        if (userAction.getUserActionType() == UserActionType.LOGIN){
            messageToSend = this.loginHandle(((LoginUserAction)userAction).getNickname());
        }

        else if(userAction.getUserActionType() == UserActionType.USE_CHARACTER ||
                userAction.getUserActionType() == UserActionType.USE_ABILITY){
            messageToSend = this.characterRequestHandle(userAction);
        }

        else {
            if (gameStarted && !turnController.getCurrentPlayer().equals(nickname)) {
                //If game started, then only a specific user can take action. Before it starts, anyone can send their selection to make process faster
                messageToSend = new IllegalActionError("You can only act during your turn.");
            }
            else if (!lastRequestMade.get(nickname).getExpectedUserAction().equals(userAction.getUserActionType())) {
                //User is always expected to send userAction that was last requested. The only exception are Login and Character actions (accounted and treated separately above)
                messageToSend = new IllegalActionError("Action taken is not the action requested, nor a Login or Character action");
            }
            else {
                switch (userAction.getUserActionType()) {
                    case GAME_SETTINGS -> {
                        int n = ((GameSettingsUserAction) userAction).getNumOfPlayers();
                        GameMode gameMode = ((GameSettingsUserAction) userAction).getGameMode();
                        messageToSend = this.selectGameSettings(n, gameMode);
                        this.createGame();
                    }
                    case TOWER_COLOR -> {
                        TowerColor towerColor = ((TowerColorUserAction) userAction).getTowerColor();
                        messageToSend = this.addPlayer(nickname, towerColor);
                    }
                    case WIZARD -> {
                        WizardType wizardType = ((WizardUserAction) userAction).getWizardType();
                        messageToSend = this.chooseWizard(nickname ,wizardType);
                        if(playersReady == numOfPlayers)
                            messageToSend = this.startGame();
                    }
                    case PLAY_ASSISTANT -> {
                        int assistantID = ((PlayAssistantUserAction) userAction).getAssistantID();
                        messageToSend = this.playAssistant(nickname, assistantID);
                    }
                    case MOVE_STUDENT -> {
                        int studentID = ((MoveStudentUserAction) userAction).getStudentID();
                        int islandOrTableID = ((MoveStudentUserAction) userAction).getIslandOrTableID();
                        messageToSend = this.moveStudentFromEntrance(nickname, studentID, islandOrTableID);
                    }
                    case MOVE_MOTHER_NATURE -> {
                        int islandID = ((MoveMotherNatureUserAction) userAction).getIslandID();
                        messageToSend = this.moveMotherNature(nickname, islandID);
                    }
                    case TAKE_FROM_CLOUD -> {
                        int cloudID = ((TakeFromCloudUserAction) userAction).getCloudID();
                        messageToSend = this.takeFromCloud(nickname, cloudID);
                    }
                }
            }
        }
        if(messageToSend != null)
            sendToUser(messageToSend);
    }

    /**
     * Handles login of a given player. It is assumed that the received nickname is unique.
     * If the player who connects is the first to do so, starts by requesting game mode parameters.
     * If it isn't, adds the player only if the game parameters have been chosen and the game isn't already full,
     * otherwise returns a login error.
     * To those players, it requests the tower color.
     * In either case, the first request from the controller is sent here, and thus here the
     * main request/userAction loop with user starts.
     * @param nickname nickname of the player trying to join the game. Assumed unique.
     * @return either an error or the first request to send the client
     */
    private Message loginHandle(String nickname){ //Check on username uniqueness done by SERVER (needs to be unique between ALL games and controllers. Assumed here unique.)
        if(players.size() == 0){ //If first to login
            players.add(nickname);
            Request request = new Request(nickname,"Select number of players", UserActionType.GAME_SETTINGS);
            request.addRequestParameter(RequestParameter.NUM_OF_PLAYERS);
            request.addRequestParameter(RequestParameter.GAME_MODE);
            return request;
        }
        else{
            if(numOfPlayers != 0 && gameMode != null){ //If first player has chosen game settings
                if(players.size() < numOfPlayers){ //If there is still space in the game
                    players.add(nickname);
                    Request request = new Request(nickname, "Select TowerColor", UserActionType.TOWER_COLOR);
                    request.addRequestParameter(RequestParameter.TOWER_COLOR);
                    return request;
                }
                else { //If there is no more space
                    return new LoginError("Lobby is complete!");
                }
            }
            else{ //If he has yet to choose game settings
                return new LoginError("Lobby has not yet chosen game settings.");
                //TODO: return error to server: it should put this user in a queue and add him to the first game which
                // is ready (assuming multiple games, if not the queue can be kept in controller)
            }
        }
    }

    /**
     * Handles UseCharacter and UseAbility UserActions.
     * Both are denied if the game isn't an expert game, if the game hasn't started yet, if the player sending
     * is not the current player and if it isn't the action phase.
     * Additionally, UseAbility UserAction gets denied if no character had previously been activated and if
     * no uses are left.
     * If all these conditions are met, it tries to activate the character or its ability, and retrieves the
     * next character request, which is then returned.
     * @param userAction the action taken by the user.
     * @return either an error or the next character request to send the client
     */
    private Message characterRequestHandle(UserAction userAction){
        String nickname = userAction.getSender();

        if(gameMode != GameMode.EXPERT){
            return  new IllegalActionError("Can't activate character in Normal game mode.");
        }
        else  if(!gameStarted){
            return new IllegalActionError("Game hasn't started yet, can't play character");
        }
        else if(!turnController.getCurrentPlayer().equals(nickname)){
            return new IllegalActionError("Can't play a character during another players turn!");
        }
        else if(turnController.getCurrentPhase() != Phase.ACTION){
            return new IllegalActionError("Can't play a character during Planning phase!");
        }
        else{
            Message characterRequest = null;
            switch (userAction.getUserActionType()){
                case USE_CHARACTER -> {
                    int characterID = ((UseCharacterUserAction) userAction).getCharacterID();
                    characterRequest = this.useCharacter(nickname, characterID);
                }
                case USE_ABILITY -> {
                    if(characterAbilityRequest == null){ //No character was activated this turn, no ability can be used
                        return new IllegalActionError("No character was activated this turn");
                    }
                    else if(characterAbilityRequest.getUsesLeft() == 0){ //Last request sent had no more uses left, thus ability can't be activated
                        return new IllegalActionError("No more uses left for character ability");
                    }
                    else {
                        List<Integer> requestParameters = ((UseAbilityUserAction) userAction).getRequestedParameters();
                        characterRequest = this.useAbility(nickname, requestParameters);
                    }
                }
            }
            return characterRequest;
        }
    }

    /**
     * Method that parses a generic message and sends it correctly.
     * @param message the generic message to send.
     */
    private void sendToUser(Message message){
        if(message instanceof EndGameRequest){
            sendEndGameToUsers((EndGameRequest) message);
        }
        else if(message instanceof CharacterRequest){
            sendCharacterRequestToUser((CharacterRequest) message);
        }
        else if(message instanceof Request){
            sendRequestToUser((Request) message);
        }
        else if(message instanceof Error){
            sendErrorToUser((Error) message);
        }
    }

    /**
     * Method to send a request to the user. It asks the server to do this operation.
     * It also saves the request sent as the last request sent to that user.
     * @param request the request to send.
     */
    private void sendRequestToUser(Request request){
        lastRequestMade.put(request.getRecipient(), request);
        //TODO: server.sendToUser()
    }

    /**
     * Method to send a character request to the user. It asks the server to do this operation.
     * It also saves the request sent, to remember character that was used.
     * @param request the request to send.
     */
    private void sendCharacterRequestToUser(CharacterRequest request){
        characterAbilityRequest = request;
        //TODO: server.sendToUser()
    }

    /**
     * Method to send errors
     * @param error error to send
     */
    public void sendErrorToUser(Error error){
        //TODO: server.sendToUser()
    }

    public void sendEndGameToUsers(EndGameRequest endGameRequest){
        for (String nickname : players){
            //TODO: server.sendToUser()
        }
    }

    //endregion

    //region Game handle

    /**
     * Sets the game settings. Returns a tower color request.
     * @param numOfPlayers the number of players of the game.
     * @param gameMode the game mode of the game.
     * @return the next request to send the client.
     */
    private Message selectGameSettings(int numOfPlayers, GameMode gameMode){
        this.numOfPlayers = numOfPlayers;
        turnController.setNumOfPlayers(numOfPlayers);
        this.gameMode = gameMode;
        Request request = new Request(players.get(0),"Select TowerColor", UserActionType.TOWER_COLOR);
        request.addRequestParameter(RequestParameter.TOWER_COLOR);
        return request; //Sent to first who connected, which is the ONLY player connected right now
    }

    /**
     * Creates a game.
     */
    private void createGame(){
        game = gameFactory.create(numOfPlayers, gameMode);
        turnController.setGame(game);
    }

    /**
     * Adds player to the game. Returns an error if the team of that TowerColor is already full. Otherwise,
     * returns a wizard request.
     * @param nickname the player who chose.
     * @param towerColor the tower color chosen.
     * @return either an error or the next request to send the client.
     */
    private Message addPlayer(String nickname, TowerColor towerColor){
        try{
            game.createPlayer(nickname, towerColor);
        }
        catch (IllegalArgumentException e){
            return new IllegalActionError( towerColor + " is already taken!");
        }
        Request request = new Request(nickname,"Choose a wizard", UserActionType.WIZARD);
        request.addRequestParameter(RequestParameter.WIZARD);
        return request;
    }

    /**
     * Assigns wizard to a player. Returns an error if wizard was already chosen. Otherwise, returns null
     * since before sending the next request all player must have chosen their wizard.
     * @param nickname the player who chose.
     * @param wizardType the wizard chosen.
     * @return either an error or null.
     */
    private Message chooseWizard(String nickname,WizardType wizardType){
        try {
            game.assignWizard(nickname, wizardType);
        }catch (IllegalArgumentException e){
            return new IllegalActionError( wizardType + " is already taken!");
        }
        playersReady++;
        return null;
    }

    /**
     * Starts the actual game. Returns the first play assistant request to the (random) first player.
     * @return the play assistant request to send to the first user.
     */
    private Message startGame(){
        turnController.startGame();
        gameStarted = true;
        if(gameMode == GameMode.EXPERT){
            ((GameExpert) game).distributeInitialCoins();
            ((GameExpert) game).createCharacters();
        }
        game.refillClouds();
        //At start of game, first request is to the current player (randomly drawn by model) to play an assistant
        Request request = new Request(turnController.getCurrentPlayer(),"Play an assistant card from your deck", UserActionType.PLAY_ASSISTANT);
        request.addRequestParameter(RequestParameter.ASSISTANT);
        return request;
    }

    /**
     * Plays the assistant chosen. Returns an error if no card with such ID exists or if card can't be played.
     * If no error occurs, either sends a play assistant request to the next player or changes the phase and
     * sends a move student request to the first player of th action phase.
     * @param nickname the player who played the card.
     * @param assistantID the ID of the assistant to play.
     * @return either an error or the next request to send.
     */
    private Message playAssistant(String nickname, int assistantID){
        try { //Tries to play assistant selected
            game.playAssistant(nickname, assistantID);
        }catch (IllegalArgumentException | NoSuchElementException | LastRoundException e){ //If played assistant is invalid
            if( e instanceof LastRoundException){
                this.lastRoundOperations();
            }
            else if(e instanceof NoSuchElementException){
                return new IllegalActionError(e.getLocalizedMessage());
            }
            else {//if(e instanceof IllegalArgumentException)
                return new IllegalActionError(e.getLocalizedMessage());
            }
        }

        Request request;
        try{ //If another player has to play an assistant, it will send them a request to play it
            turnController.nextTurn();
            request = new Request(turnController.getCurrentPlayer(),"Play an assistant card from your deck", UserActionType.PLAY_ASSISTANT);
            request.addRequestParameter(RequestParameter.ASSISTANT);
        }catch (IllegalStateException phaseDone){
            //If all players have played their assistant, it will progress the phase and ask for next request
            turnController.nextPhase(); //Will compute the Action order, request made to player who played lowest card
            request = new Request(turnController.getCurrentPlayer(),"Move a student from your entrance", UserActionType.MOVE_STUDENT);
            request.addRequestParameter(RequestParameter.STUDENT_ENTRANCE);
            request.addRequestParameter(RequestParameter.ISLAND_OR_TABLE);
        }
        return request;
    }

    /**
     * Move the student from the entrance to the selected destination. Returns an error if the action selected
     * is illegal. If no error occurs, checks if the right amount of students have been moved this turn.
     * If there are students to move, the last request is re-made. Otherwise, a move mother nature request is sent.
     * @param nickname the player who moved the student
     * @param studentID the ID of the student to move
     * @param destinationID the ID of the destination (table or island)
     * @return either an error or the next request to send.
     */
    private Message moveStudentFromEntrance(String nickname, int studentID, int destinationID){
        try {
            game.moveStudentFromEntrance(nickname, studentID, destinationID);
        }catch (FullTableException e){
            return new IllegalActionError(e.getLocalizedMessage());

        }

        Request request;
        if(turnController.studentMoved() == turnController.getStudentsToMove()){
            //If no more students to move, makes a new request to move mother nature
            request = new Request(turnController.getCurrentPlayer(), "Move mother nature", UserActionType.MOVE_MOTHER_NATURE);
            request.addRequestParameter(RequestParameter.ISLAND);
        }
        else { //If more students to move, resends last request (to move a student)
            request = lastRequestMade.get(nickname);
        }
        return request;
    }

    /**
     * Moves mother nature to the selected island. Returns an error if the chosen island is beyond player's reach.
     * If the game ends (either because only 3 groups remain or a team has no more towers) an end game request is
     * sent. Then, a take from cloud request is ent if this isn't le last round. If it is, take from cloud is skipped
     * and endRoundOperations are performed (which will return an end game request, since it is the last round).
     * @param nickname the player who move mother nature.
     * @param islandID the destination island of mother nature
     * @return either an error, the next request to send or an end game request.
     */
    private Message moveMotherNature(String nickname, int islandID){
        try { //If move is valid, mother nature is moved (and all other consequent operations are done by the model),
            // then next request is sent to choose a cloud to refill entrance
            game.moveMotherNature(nickname, islandID);
        }
        catch (IllegalArgumentException | GameOverException e){ //If it isn't valid error and action must be retaken. If game is over, message
            if(e instanceof  GameOverException){
                return this.gameOverOperations();
            }
            else{
                return new IllegalActionError(e.getLocalizedMessage());
            }
        }
        Request request;
        if(!isLastRound){
            request = new Request(turnController.getCurrentPlayer(),"Select a cloud", UserActionType.TAKE_FROM_CLOUD);
            request.addRequestParameter(RequestParameter.CLOUD);
        }
        else //If it's the last round, clouds are disabled and cloud selection is skipped (since useless, it's the last round no one cares)
            //CHECKME: always to disable clouds?
            return this.endOfRoundOperations(); //Since its last round, EndGameRequest will be sent.
        return request;
    }

    /**
     * Takes contents of a cloud and puts them in the user's entrance. Returns error if cloud was already taken.
     * Otherwise, it either returns a move student request to the next player or, if all players have finished their
     * action phase, performs end of round operations (which return the next request to make).
     * @param nickname the player who took from the cloud.
     * @param cloudID the cloud selected.
     * @return either an error or the next request to send.
     */
    private Message takeFromCloud(String nickname, int cloudID){
        try {
            game.takeFromCloud(nickname, cloudID);
        }
        catch (NoSuchElementException e){
            return new IllegalActionError(e.getLocalizedMessage());
            //TODO: other exceptions should be thrown and handled
        }

        Request request;
        try { //If there is a next player that has to play its Action phase, sends request to him
            turnController.nextTurn();
            characterAbilityRequest = null; //reset character usage
            request = new Request(turnController.getCurrentPlayer(), "Move a student from your entrance", UserActionType.MOVE_STUDENT);
            request.addRequestParameter(RequestParameter.STUDENT_ENTRANCE);
            request.addRequestParameter(RequestParameter.ISLAND_OR_TABLE);
        }
        catch (IllegalStateException phaseDone){ //If instead all players have played, phase is switched
            return this.endOfRoundOperations();
        }
        return request;
    }

    /**
     * Uses (in the sense that it gets paid for) a character. Returns an error if the character can't be used.
     * Otherwise, returns character request.
     * If no selection is required to the user, the ability is immediately activated, then a character
     * request is returned.
     * @param nickname the player who used the character.
     * @param characterID the ID of the character used.
     * @return either an error or the character request to send.
     */
    private Message useCharacter(String nickname, int characterID){
        List<RequestParameter> requestParameters;
        try{
            requestParameters = ((GameExpert) game).useCharacter(nickname, characterID);
        }
        catch (IllegalStateException e){
            return new IllegalActionError(e.getLocalizedMessage());
        }

        CharacterRequest request = new CharacterRequest(nickname,
                "Parameters requested to use character ability",
                UserActionType.USE_ABILITY,
                ((GameExpert) game).getActiveCharacterMaxUses());
        request.addAllRequestParameters(requestParameters);
        if(requestParameters.size() != 0)   //If no selection is required, ability is instantly activated,
            // request is sent to user just to confirm its activation, client won't respond to a request with no parameters, also because it will have 0 usesLeft
            return useAbility(nickname, new ArrayList<>());
        return request;
    }

    /**
     * Uses the ability of the active character. If the selection contains wrong values, an Error is returned.
     * Otherwise, the ability is used and a new character request is returned with one less use left.
     * @param nickname the player who used the ability.
     * @param parameters the parameters to use the ability.
     * @return either an error or the character request to send.
     */
    private Message useAbility(String nickname, List<Integer> parameters){
        try {
            ((GameExpert) game).useAbility(parameters);
        }
        catch (IllegalStateException | LastRoundException | GameOverException e){
            if(e instanceof  GameOverException){
                return this.gameOverOperations();
            }
            else if (e instanceof  LastRoundException){
                this.lastRoundOperations();
            }
            else{
                return new IllegalActionError(e.getLocalizedMessage());
            }
            //TODO: other exceptions should be thrown and handled
        }
        CharacterRequest request = new CharacterRequest(nickname,
                "Parameters requested to use character ability",
                UserActionType.USE_ABILITY,
                characterAbilityRequest.getUsesLeft() - 1);
        request.addAllRequestParameters(characterAbilityRequest.getRequestParameters());
        return request;
    }

    //used by above

    /**
     * Performs actions at end of round.
     * If it catches that the game has ended, calls gameOverOperations and returns an end game request.
     * Otherwise, progresses to next phase and refills the clouds, then returns a play assistant request to the
     * first player of the planning phase.
     * @return either request to send the client or an end game request
     */
    private Request endOfRoundOperations(){
        Request request ;
        try { //perform end of round operations
            game.endOfRoundOperations();
        }catch (GameOverException e){
            return this.gameOverOperations();
        }
        turnController.nextPhase(); //Will compute the Planning order, request made to player who played lowest card
        try {
            game.refillClouds();
        }catch (LastRoundException lastRound){
            this.lastRoundOperations();
        }
        request = new Request(turnController.getCurrentPlayer(),"Play an assistant card from your deck", UserActionType.PLAY_ASSISTANT);
        request.addRequestParameter(RequestParameter.ASSISTANT);
        return request;
    }

    /**
     * Operations done when the current round is set to be the last.
     * Sets the last round on the game and disables its cloud, then sets lastRound true on the controller.
     */
    private void lastRoundOperations(){
        game.setLastRound();
        game.disableClouds(); //CHECKME: always to disable?
        this.isLastRound = true;
    }

    /**
     * Determines the game winner and sens an end game request. The request contains the winner of the game and has
     * no recipient; it will later be sent to all players.
     * @return a request to end the game.
     */
    private EndGameRequest gameOverOperations(){
        TowerColor winner = game.determineWinner();
        return new EndGameRequest(null, "End the game and show winner", null, winner);
    }

    //endregion

}
