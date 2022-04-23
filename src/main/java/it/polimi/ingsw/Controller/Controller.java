package it.polimi.ingsw.Controller;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.GameFactory;
import it.polimi.ingsw.Network.Message.*;
import it.polimi.ingsw.Network.Message.Error.Error;
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

    private TurnController turnController;

    /**
     * The factory used to create the game.
     */
    private GameFactory gameFactory;

    /**
     * The game which the controller controls.
     */
    private Game game;

    private List<String> players;
    private int numOfPlayers;
    private int playersReady;
    private GameMode gameMode;

    private HashMap<String, Request> lastRequestMade;

    private boolean gameStarted;
    private boolean isLastRound;

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
     * The server receives messages (UserAction) which are forwarded to the controller though this method.
     * The method parses the type of userAction and acts accordingly.
     * @param userAction the action taken by the user.
     */
    public void receiveUserAction(UserAction userAction){
        String nickname = userAction.getSender();
        if (userAction.getUserActionType() == UserActionType.LOGIN)
            this.loginHandle(((LoginUserAction)userAction).getNickname());
        else {
            if (gameStarted && !turnController.getCurrentPlayer().equals(nickname)) {
                //If game started, then only a specific user can take action. Otherwise in login, anyone can send their selection
                //TODO: send error to server wrong player
                return;
            }
            if (!lastRequestMade.get(nickname).getExpectedUserAction().equals(userAction.getUserActionType())) {
                //At any time other than initial login, user is expected to send userAction as it was last requested
                //TODO: send error to server wrong action taken
                return;
            }
            switch (userAction.getUserActionType()) {
                case NUM_OF_PLAYERS -> {
                    int n = ((NumOfPlayersUserAction) userAction).getNumOfPlayers();
                    this.selectPlayerNumber(n);
                }
                case GAME_MODE -> {
                    GameMode gameMode = ((GameModeUserAction) userAction).getGameMode();
                    this.selectGameMode(gameMode);
                    this.createGame();
                }
                case TOWER_COLOR -> {
                    TowerColor towerColor = ((TowerColorUserAction) userAction).getTowerColor();
                    this.addPlayer(nickname, towerColor);
                }
                case WIZARD -> {
                    WizardType wizardType = ((WizardUserAction) userAction).getWizardType();
                    this.chooseWizard(nickname ,wizardType);
                    if(playersReady == numOfPlayers)
                        this.startGame();
                }
                case PLAY_ASSISTANT -> {
                    int assistantID = ((PlayAssistantUserAction) userAction).getAssistantID();
                    this.playAssistant(nickname, assistantID);
                }
                case MOVE_STUDENT -> {
                    int studentID = ((MoveStudentUserAction) userAction).getStudentID();
                    int islandOrTableID = ((MoveStudentUserAction) userAction).getIslandOrTableID();
                    this.moveStudentFromEntrance(nickname, studentID, islandOrTableID);
                }
                case MOVE_MOTHER_NATURE -> {
                    int islandID = ((MoveMotherNatureUserAction) userAction).getIslandID();
                    this.moveMotherNature(nickname, islandID);
                }
                case TAKE_FROM_CLOUD -> {
                    int cloudID = ((TakeFromCloudUserAction) userAction).getCloudID();
                    this.takeFromCloud(nickname, cloudID);
                }
                case USE_CHARACTER -> {

                }
                case USE_ABILITY -> {

                }
            }
        }
    }

    public void sendRequestToUser(Request request, String nicknameReceiver){
        lastRequestMade.put(nicknameReceiver, request);
        //TODO: server.sendToUser()
    }

    public void sendError(Error error){

    }
    //endregion

    //region Game creation

    /**
     * Handles login of a given player. It is assumed that the received nickname is unique.
     * If the player who connects is the first to do so, starts by requesting game mode parameters.
     * If it isn't, adds the player only if the game parameters have been chosen and the game isn't already full.
     * To those players, it requests the tower color.
     * In either case, the first request from the controller is sent here, and thus here the
     * main request/userAction loop with user starts.
     * @param nickname nickname iof the player trying to join the game. Assumed unique.
     */
    private void loginHandle(String nickname){ //Check on username uniqueness done by SERVER (needs to be unique between ALL games and controllers. Assumed here unique.)
        if(players.size() == 0){ //If first to login
            players.add(nickname);
            Request request = new Request("Select number of players", UserActionType.NUM_OF_PLAYERS); //FIXME: unite num of players and game mode
            request.addRequestParameter(RequestParameter.NUM_OF_PLAYERS);
            sendRequestToUser(request, nickname);
        }
        else{
            if(numOfPlayers != 0 && gameMode != null){ //If first player has chosen game settings
                if(players.size() < numOfPlayers){ //If there is still space in the game
                    players.add(nickname);
                    Request request = new Request("Select TowerColor", UserActionType.TOWER_COLOR);
                    request.addRequestParameter(RequestParameter.TOWER_COLOR);
                    sendRequestToUser(request, nickname);
                }
                else { //If there is no more space
                    //TODO: return error to server game complete
                }
            }
            else{ //If he has yet to choose game settings
                //TODO: return error to server: it should put this user in a queue and add him to the first game which
                // is ready (assuming multiple games, if not the queue can be kept in controller)
            }
        }
    }

    private void selectPlayerNumber(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
        turnController.setNumOfPlayers(numOfPlayers);
        Request request = new Request("Select game mode", UserActionType.GAME_MODE);
        request.addRequestParameter(RequestParameter.GAME_MODE);
        sendRequestToUser(request, players.get(0)); //Sent to first who connected, which is the ONLY player connected right now
    }

    private void selectGameMode(GameMode gameMode){
        this.gameMode = gameMode;
        Request request = new Request("Select TowerColor", UserActionType.TOWER_COLOR);
        request.addRequestParameter(RequestParameter.TOWER_COLOR);
        sendRequestToUser(request, players.get(0)); //Sent to first who connected, which is the ONLY player connected right now
    }

    private void createGame(){
        game = gameFactory.create(numOfPlayers, gameMode);
        turnController.setGame(game);
    }

    private void addPlayer(String nickname, TowerColor towerColor){
        try{
            game.createPlayer(nickname, towerColor);
            Request request = new Request("Choose a wizard", UserActionType.WIZARD);
            request.addRequestParameter(RequestParameter.WIZARD);
            sendRequestToUser(request, nickname);
        }
        catch (IllegalArgumentException e){
            //TODO: send error to server
        }
    }

    private void chooseWizard(String nickname,WizardType wizardType){
        try {
            game.assignWizard(nickname, wizardType);
            playersReady++;
        }catch (IllegalArgumentException e){
            //TODO: send error to server
        }
    }
    //endregion

    //region Game state changer

    private void startGame(){
        turnController.startGame();
        gameStarted = true;
        this.refillClouds();
        //At start of game, first request is to the current player (randomly drawn by model) to play an assistant
        Request request = new Request("Play an assistant card from your deck", UserActionType.PLAY_ASSISTANT);
        request.addRequestParameter(RequestParameter.ASSISTANT);
        sendRequestToUser(request, turnController.getCurrentPlayer());
    }

    //called in receiveMessage

    private void playAssistant(String nickname, int assistantID){
        try { //Tries to play assistant selected
            game.playAssistant(nickname, assistantID);
            try{ //If another player has to play an assistant, it will send them a request to play it
                turnController.nextTurn();
                Request request = new Request("Play an assistant card from your deck", UserActionType.PLAY_ASSISTANT);
                request.addRequestParameter(RequestParameter.ASSISTANT);
                sendRequestToUser(request, turnController.getCurrentPlayer());
            }catch (IllegalStateException phaseDone){
                //If all players have played their assistant, it will progress the phase and ask for next request
                turnController.nextPhase(); //Will compute the Action order, request made to player who played lowest card
                Request request = new Request("Move a student from your entrance", UserActionType.MOVE_STUDENT);
                request.addRequestParameter(RequestParameter.STUDENT_ENTRANCE);
                request.addRequestParameter(RequestParameter.ISLAND_OR_TABLE);
                sendRequestToUser(request, turnController.getCurrentPlayer());
            }
        }catch (IllegalArgumentException | NoSuchElementException | LastRoundException e){ //If played assistant is invalid
            if( e instanceof LastRoundException){
                this.lastRoundOperations();
            }
            //TODO: send error to server
        }
    }

    private void moveStudentFromEntrance(String nickname, int studentID, int destinationID){
        try {
            game.moveStudentFromEntrance(nickname, studentID, destinationID);
            if(turnController.studentMoved() == turnController.getStudentsToMove()){
                //If no more students to move, makes a new request to move mother nature
                Request request = new Request("Move mother nature", UserActionType.MOVE_MOTHER_NATURE);
                request.addRequestParameter(RequestParameter.ISLAND);
                sendRequestToUser(request, turnController.getCurrentPlayer());
            }
            else { //If more students to move, resends last request (to move a student)
                sendRequestToUser(lastRequestMade.get(nickname), turnController.getCurrentPlayer());
            }
        }catch (FullTableException e){
            //TODO: send error to server
        }
    }

    private void moveMotherNature(String nickname, int islandID){
        try { //If move is valid, mother nature is moved (and all other consequent operations are done by the model),
            // then next request is sent to choose a cloud to refill entrance
            game.moveMotherNature(nickname, islandID);
            if(!isLastRound){
                Request request = new Request("Select a cloud", UserActionType.TAKE_FROM_CLOUD);
                request.addRequestParameter(RequestParameter.CLOUD);
                sendRequestToUser(request, turnController.getCurrentPlayer());
            }
            else //If it's the last round, clouds are disabled and cloud selection is skipped (since useless, it's the last round no one cares)
                //CHECKME: always to disable?
                this.endOfRoundOperations();
        }
        catch (IllegalArgumentException | GameOverException e){ //If it isn't valid error and action must be retaken. If game is over, message
            if(e instanceof  GameOverException){
                this.gameOverOperations();
            }
            //TODO: send error to server
        }
    }

    private void takeFromCloud(String nickname, int cloudID){
        game.takeFromCloud(nickname, cloudID);
        try { //If there is a next player that has to play its Action phase, sends request to him
            turnController.nextTurn();
            Request request = new Request("Move a student from your entrance", UserActionType.MOVE_STUDENT);
            request.addRequestParameter(RequestParameter.STUDENT_ENTRANCE);
            request.addRequestParameter(RequestParameter.ISLAND_OR_TABLE);
            sendRequestToUser(request, turnController.getCurrentPlayer());
        }
        catch (IllegalStateException phaseDone){ //If instead all players have played, phase is switched
            this.endOfRoundOperations();
        }
    }

    //used by above

    private void refillClouds() throws LastRoundException{
        game.refillClouds();
    }

    /**
     * Performs actions at end of round and catches whether the game has ended. If it has, calls gameOverOperations
     * and returns. Otherwise, progresses to next phase and refills the clouds
     */
    private void endOfRoundOperations(){
        try { //perform end of round operations
            game.endOfRoundOperations();
        }catch (GameOverException e){
            this.gameOverOperations();
            return;
        }
        turnController.nextPhase(); //Will compute the Planning order, request made to player who played lowest card
        try {
            this.refillClouds();
        }catch (LastRoundException lastRound){
            this.lastRoundOperations();
        }
    }

    private void lastRoundOperations(){
        game.setLastRound();
        game.disableClouds(); //CHECKME: always to disable?
        this.isLastRound = true;
    }

    private void gameOverOperations(){
        TowerColor winner = game.determineWinner();
        //TODO: send endgame/winning message
    }
    //endregion
}
