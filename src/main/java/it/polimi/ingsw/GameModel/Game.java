package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CloudTile;
import it.polimi.ingsw.GameModel.Board.Player.AssistantCard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Table;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;


import java.io.Serializable;
import java.util.*;

/**
 * Main class of the GameModel. This class acts as a hotpoint for the classes below it, that deal
 * with board pieces and pawns. It's also the interface to which the controller communicates and
 * calls methods. Finally, it coordinates game actions between the main components of the Model:
 * the Clouds, the Bag and the Coin Bag; the ProfessorSet storing Professors; the Archipelago storing
 * islands and Mother nature; the Players owning a board and a deck; the CharacterManager storing and
 * activating Characters; the TurnManager that decides the turn order and the current player.
 */
public class Game implements ObservableByClient, Serializable {

    protected final Bag bag;
    protected final Archipelago archipelago = new Archipelago();
    private final List<CloudTile> clouds = new ArrayList<>();
    protected final ProfessorSet professorSet = new ProfessorSet();
    protected final PlayerList players = new PlayerList();
    private final TurnManager turnManager = new TurnManager();

    /**
     * Linked hashmap that stores the Assistant cards played this round, and by whom they were played.
     */
    private final LinkedHashMap<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();

    /**
     * Linked hashmap that stores the Assistant cards played last round, and by whom they were played. Used for rendering
     */
    private final Map<Player, AssistantCard> cardsPlayedLastRound = new LinkedHashMap<>();

    /**
     * Boolean to store whether this is the last round to play. Gets set when a LastRoundException is thrown
     */
    private boolean isLastRound;

    /**
     * The configuration of this game
     */
    private final GameConfig gameConfig;

    private TowerColor winner;


    /**
     * Constructor for Game. Places 10 students across the Archipelago, fills the bag with the
     * remaining students, then sets up the number and size of clouds and of players. Finally, adds
     * the Players to the TurnManager.
     * @param gameConfig the game configuration object, created by the GameFactory
     */
    public Game(GameConfig gameConfig) {
        bag = new Bag();
        isLastRound = false;
        this.gameConfig = gameConfig;
        archipelago.initialStudentPlacement(bag.drawN(10));
        bag.fillRemaining();
        gameConfig.getPlayerConfig().setBag(bag);

        for (int i = 0; i < gameConfig.getNumOfClouds(); i++)
            clouds.add(new CloudTile(gameConfig.getCloudSize(), bag));
        winner = null;
    }

    /**
     * Getter for a Player by their nickname.
     * @param nickname nickname of the player to find
     * @return the player with the given nickname if found
     * @throws NoSuchElementException when no player in the game has the given nickname
     */
    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        Player playerToReturn = null;
        for (Player player : players) {
            if(player.getNickname().equals(nickname))
                playerToReturn = player;
        }
        if (playerToReturn == null) throw new NoSuchElementException("Player not found");
        return playerToReturn;
    }

    /**
     * Creates a new player in the current game.
     * @param nickname Nickname of the player. Assumed unique.
     * @param towerColor The team chosen by the player.
     * @throws IllegalArgumentException Thrown when team selected is already full.
     */
    public void createPlayer(String nickname, TowerColor towerColor) throws  IllegalArgumentException{
        int teamSize = gameConfig.getNumOfPlayers() / 2;
        if(players.getTeam(towerColor).size() == teamSize)
            throw new IllegalArgumentException("Team "+ towerColor + " is already full");
        else{
            boolean isTowerHolder = players.getTowerHolder(towerColor) == null;
            players.add(new Player(nickname, towerColor,isTowerHolder, gameConfig.getPlayerConfig()));
            turnManager.addPlayerClockwise(players.getByNickname(nickname));
        }
    }

    /**
     * Method that assigns a chosen Wizard and their deck to a player's hand.
     * @param nickname the nickname of the player who will own the deck
     * @param wizardType the type of the wizard
     * @throws IllegalArgumentException when the wizard has already been chosen in this game
     */
    public void assignWizard(String nickname, WizardType wizardType) throws IllegalArgumentException {
        if (getWizardTypes().contains(wizardType)) throw new IllegalArgumentException("Wizard type already chosen");
        Player player = getPlayerByNickname(nickname);
        player.pickWizard(wizardType);
    }

    /**
     * Getter for the wizard types that every player chose in this game.
     * @return a list with the wizard types already chosen in the game
     */
    private List<WizardType> getWizardTypes() { // can be made using streams
        List<WizardType> wizardTypes = new ArrayList<>();
        for (Player player : players) {
            wizardTypes.add(player.getWizardType());
        }
        return wizardTypes;
    }

    /**
     * Method that determines the planning order of the very first round, and actually starts the game
     * (game phase goes from idle to planning)
     */
    public void determineFirstRoundOrder() {
        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // set from idle -> planning
    }

    /**
     * Method that retrieves the played AssistantCard by its ID and passes its turn order to TurnManager.
     * It also checks if a Player is "desperate", that is, if they can only play Assistants that
     * have already been played by someone else.
     * @param nickname the nickname of the Player who is playing their Assistant
     * @param assistantID the ID of the AssistantCard chosen
     * @throws IllegalArgumentException if a player is not "desperate" and chooses an Assistant card already chosen by someone else
     */
    public void playAssistant(String nickname, int assistantID) throws IllegalArgumentException,NoSuchElementException,LastRoundException {
        if (!checkDesperate(nickname)) {
            for (AssistantCard assistantCard : cardsPlayedThisRound.values()) {
                if (assistantCard.getID() == assistantID)
                    throw new IllegalArgumentException("Assistant card already chosen");
            }
        }
        AssistantCard assistantPlayed = getPlayerByNickname(nickname).playAssistant(assistantID);
        cardsPlayedThisRound.put(getPlayerByNickname(nickname), assistantPlayed);
        if(getPlayerByNickname(nickname).getDeck().size() == 0 && !isLastRound) { //Only throws once, needless to throw for each player (once one is done, it is the lastRound for everyone)
            throw new LastRoundException("Player "+nickname+" has played last assistant");
        }
    }

    /**
     * Method that checks if a certain Player is "desperate", that is, if they can only play
     * Assistants that have already been played by someone else.
     * @param nickname the Player to check
     * @return true if the Player is "desperate", false otherwise
     */
    public boolean checkDesperate(String nickname) {
        return getPlayerByNickname(nickname).checkDesperate(cardsPlayedThisRound.values());
    }

    /**
     * Method for moving a student from the player's entrance to the dining room or to an island.
     * It checks if the student is actually inside the entrance, if the destination is a dining
     * room table, or if it is an island tile.
     * @param nickname the nickname of the entrance's owner
     * @param studentID the ID of the entrance student to move
     * @param containerID the ID of the student container which will host the student
     */
    public void moveStudentFromEntrance(String nickname, int studentID, int containerID) throws FullTableException {
        Player player = getPlayerByNickname(nickname);
        Student student = player.getStudentFromEntrance(studentID);
        Table potentialTable = player.getTable(student.getColor());
        if (potentialTable.getID() == containerID){
            if(!potentialTable.isFull()){
                student.getStudentContainer().removePawn(student);
                potentialTable.placeStudent(student);
                checkAndMoveProfessor(players, student.getColor());
            }
            else
                throw new FullTableException("Can't add a student to a full table!");
        }
        else {
            archipelago.placeStudent(student, archipelago.getIslandTileByID(containerID)); // will have to throw exception
        }
    }

    /**
     * Method that checks if any Player has gained the right to host the Professor of the given color
     * by moving students into their dining room.
     * @param players the list of players inside the game
     * @param color the color of the Professor to check
     */
    public void checkAndMoveProfessor(PlayerList players, Color color) {
        professorSet.checkAndMoveProfessor(players, color); // this in turn calls a checkAndMoveProfessor strategy
    }

    /**
     * Method that moves mother nature on the chosen island, provided the current player's assistant
     * card has a move power equal or greater than the number of hops required for mother nature to
     * land on that island tile. Should throw an exception
     * @param nickname nickname of the Player that is moving Mother Nature
     * @param islandTileID the ID of the IslandTile where the player wants to place Mother Nature
     */
    public void moveMotherNature(String nickname, int islandTileID) throws IllegalArgumentException, GameOverException {
        archipelago.moveMotherNature(islandTileID, cardsPlayedThisRound.get(players.getByNickname(nickname)).getMovePower());
        resolveIslandGroup(archipelago.getIslandGroupID(islandTileID));
    }

    /**
     * Method that "resolves" an island group, that is, it determines what player has the most
     * influence on that group, places one (or more) of that player's towers on the group if necessary
     * and finally checks if a merge is possible with the groups on the right or left of that group.
     * @param islandGroupID the group to resolve
     * @throws GameOverException if the total number of groups in the archipelago at the end of the process is 3 (or less)
     */
    public void resolveIslandGroup(int islandGroupID) throws GameOverException {
        archipelago.resolveIslandGroup(islandGroupID, players, professorSet);
    }

    /**
     * Method to take all students from a CloudTile and place them in the current player's entrance.
     * If the cloud in question is not selectable because it has already been picked or because this
     * is the last round and the bag is empty nothing happens.
     *
     * @param nickname the nickname of the player who is taking the students from the cloud
     * @param cloudID the ID of the cloud that the player chose
     */
    public void takeFromCloud(String nickname, int cloudID) throws NoSuchElementException{
        List<Student> studentsTaken = new ArrayList<>(
                clouds.stream()
                .filter(cloud -> cloud.getID() == cloudID && cloud.isSelectable())
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Cloud was already taken!"))
                .removeAll());

        getPlayerByNickname(nickname).addToEntrance(studentsTaken);
    }

    /**
     * Method that is called by the controller and that is forwarded to TurnManager. It determines
     * the order in which players will play their planning phase.
     */
    public void determinePlanningOrder() {
        turnManager.determinePlanningOrder();
    }

    /**
     * Method that is called by the controller and that is forwarded to TurnManager. It determines
     *      * the order in which players will play their action phase, using cardsPlayedThisRound.
     */
    public void determineActionOrder() {
        turnManager.determineActionOrder(cardsPlayedThisRound);
    }

    /**
     * This method progresses the turn, updating currentPlayer.
     */
    public void nextTurn() throws IllegalStateException{
        turnManager.nextTurn();
    }

    /**
     * This method progresses the phase, going from planning to action and vice-versa.
     */
    public void nextPhase(){
        turnManager.nextPhase();
    }

    /**
     * Method that moves the Assistant cards played this round into their respective discard pile.
     */
    public void pushThisRoundInLastRound() {
        cardsPlayedLastRound.clear();
        cardsPlayedLastRound.putAll(cardsPlayedThisRound);
        cardsPlayedThisRound.clear();
    }

    public void refillClouds() throws LastRoundException{
            for(CloudTile c : clouds){ c.fill(); }
    }

    /**
     * Method that should be called by the controller when it catches a LastRoundException. In fact
     * during the last round it is not possible to draw from CloudTiles.
     */
    public void disableClouds() {
        for (CloudTile c : clouds) c.removeAll();
    }

    /**
     * Method that should be called by the controller when it catches a LastRoundException.
     */
    public  void setLastRound(){
        isLastRound = true;
    }

    /**
     * Method that performs operation each end of round (= when the last player has played his ActionPhase turn).
     * It removes the cards played this round then checks if it's the last round.
     * @throws GameOverException if it is the last round
     */
    public void endOfRoundOperations() throws GameOverException {
        pushThisRoundInLastRound();
        if(isLastRound)
            throw new GameOverException();
        else {
            return; // the game can continue
        }
    }

    /**
     * Determines the winner of the game.
     * @return The TowerColor of the winning team, NEUTRAL if the game ended on a tie.
     */
    public TowerColor determineWinner(){ //public: controller will ask for it when it receives GameOverException
        Player currentlyWinning = players.get(0);
        boolean isDraw = false;

        for (Player player : players.getTowerHolders()) {
            if(player != currentlyWinning){
                if (player.getTowersPlaced() > currentlyWinning.getTowersPlaced()) {
                    currentlyWinning = player;
                    isDraw = false;
                } else if (player.getTowersPlaced() == currentlyWinning.getTowersPlaced()){
                    Player moreProf = professorSet.determineStrongestPlayer(player, currentlyWinning);
                    if(moreProf != null){
                        currentlyWinning = moreProf;
                        isDraw = false;
                    }
                    else
                        isDraw = true;
                }
            }
        }
        winner = isDraw ? TowerColor.NEUTRAL : currentlyWinning.getTowerColor();
        return winner;
    }


    //region State observer methods

        //region Game
        /**
         * Getter for the number of players selected for this game
         * @return the number of players
         */
        @Override
        public int getNumOfPlayers(){
            return gameConfig.getNumOfPlayers();
        }

        /**
         * Getter for the game mode selected for this game
         * @return the game mode selected for this game
         */
        @Override
        public GameMode getGameMode(){
            return this instanceof GameExpert ? GameMode.EXPERT : GameMode.NORMAL;
        }

        /**
         * Getter for the current player in the game.
         * @return the player currently executing their planning/action turn, null if firstRoundOrder
         * hasn't been determined yet
         */
        @Override
        public String getCurrentPlayer() {
            return turnManager.getCurrentPlayer() == null ? null : turnManager.getCurrentPlayer().getNickname();
        } // could be useful to controller

        /**
         * Getter for the nickname of connected players
         * @return a list with the nickname of connected players
         */
        @Override
        public List<String> getPlayers(){
            List<String> nicknames = new ArrayList<>();
            for(Player player : players)
                nicknames.add(player.getNickname());
            return nicknames;
        }

        /**
         * Getter for the teams
         * @return a hashmap with a nickname as key and the tower color as value
         */
        @Override
        public HashMap<String, TowerColor> getPlayerTeams(){
            HashMap<String, TowerColor> teams = new HashMap<>();
            for(Player player : players)
                teams.put(player.getNickname(), player.getTowerColor());
            return teams;
        }

        /**
         * Getter for the wizards chosen
         * @return a hashmap with a nickname as key and the wizard as value
         */
        @Override
        public HashMap<String, WizardType> getPlayerWizard(){
            HashMap<String, WizardType> wizards = new HashMap<>();
            for(Player player : players)
                wizards.put(player.getNickname(), player.getWizardType());
            return wizards;
        }

        /**
         * Returns current player order
         * @return a list of nicknames ordered
         */
        @Override
        public List<String> getPlayerOrder(){
            return  turnManager.getCurrentOrder();
        }

        /**
         * Returns the max amount of island groups a given player can move
         * @param nickname the player who can move the returned number of steps
         * @return the max amount of island groups a given player can move
         */
        @Override
        public int getActualMovePower(String nickname){
            if(cardsPlayedThisRound.get(players.getByNickname(nickname)) != null)
                return cardsPlayedThisRound.get(players.getByNickname(nickname)).getMovePower();
            else  return 0;
        }


        /**
             * Method used to observe cards played this round. Returned according to current order (planning or action).
             * To return them ordered, it uses the currentOrder given by TurnManager.
             * If no order has been established yet, it will return an empty LinkedHashMap.
             * @return A LinkedHashMap containing the nickname of the Player and the ID of the card played.
             */
        @Override
        public  LinkedHashMap<String, Integer> getCardsPlayedThisRound(){
            LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
            for(String nickname : turnManager.getCurrentOrder()){
                if(cardsPlayedThisRound.get(players.getByNickname(nickname)) != null)
                    result.put(nickname, cardsPlayedThisRound.get(players.getByNickname(nickname)).getID());
            }
            return  result;
        }

        /**
         * Method used to observe cards played last round. Returned according to current order (planning or action).
         * To return them ordered, it uses the currentOrder given by TurnManager.
         * If no order has been established yet, it will return an empty LinkedHashMap.
         * @return A LinkedHashMap containing the nickname of the Player and the ID of the card played
         */
        @Override
        public  LinkedHashMap<String, Integer> getCardsPlayedLastRound(){
            LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
            for(String nickname : turnManager.getCurrentOrder()){
                if(cardsPlayedLastRound.get(players.getByNickname(nickname)) != null)
                    result.put(nickname, cardsPlayedLastRound.get(players.getByNickname(nickname)).getID());
            }
            return  result;
        }

        /**
         * Method to observe the current phase of the game. Needed to let know the controller which order to calculate.
         * @return the current phase.
         */
        @Override
        public Phase getCurrentPhase(){
                return turnManager.getCurrentPhase();
            }

        /**
         * Returns the tower colors selectable
         * @return the tower colors selectable
         */
        @Override
        public List<TowerColor> getAvailableTowerColors(){
            List<TowerColor> availableTowerColors = new ArrayList<>(Arrays.stream(TowerColor.values()).toList());
            availableTowerColors.remove(TowerColor.NEUTRAL);
            if(gameConfig.getNumOfPlayers() != 3)
                availableTowerColors.remove(TowerColor.GREY);
            for (TowerColor towerColor : TowerColor.values()){
                if(players.getTeam(towerColor).size() == gameConfig.getNumOfPlayers() / 2)
                    availableTowerColors.remove(towerColor);
            }
            return availableTowerColors;
        }

        /**
         * Returns the wizards selectable
         * @return the wizards selectable
         */
        @Override
        public List<WizardType> getAvailableWizards(){
            List<WizardType> availableWizardTypes = new ArrayList<>(Arrays.stream(WizardType.values()).toList());
            for (Player player : players)
                availableWizardTypes.remove(player.getWizardType());
            return availableWizardTypes;
        }

        /**
         * Returns how many towers are left to be placed for each team
         * @return a hashmap with tower color as key and the number of towers left as key
         */
        @Override
        public HashMap<TowerColor, Integer> getTowersLeft(){
            HashMap<TowerColor, Integer> towersLeft = new HashMap<>();
            for (Player player : players){
                if(player.isTowerHolder())
                    towersLeft.put(player.getTowerColor(), player.getTowersLeft());
            }
            return towersLeft;
        }

        /**
         * Returns the team that has won the game.
         * @return the towerColor of the team who has won the game
         */
        @Override
        public TowerColor getWinner(){
            return winner;
        }
        //endregion
        //region Player
        /**
         * Returns a list of cards that weren't yet played (thus to be shown to the player)
         * @return a list of cards IDs
         */
        @Override
        public List<Integer> getCardsLeft(String nickname){
            return  players.getByNickname(nickname).getCardsLeft();
        }

        /**
         * Returns the ID of the entrance of a player, or -1 if the nickname doesn't exist
         * @param nickname the player to query
         * @return the ID of the entrance of a player, or -1 if the nickname doesn't exist
         */
        @Override
        public int getEntranceID(String nickname){
            Player p = players.getByNickname(nickname);
            if(p != null)
                return p.getEntranceID();
            else
                return -1;
        }

        /**
         * Method to observe all the students in the entrance and their color
         * @return HashMap with the student ID as key and its color as object
         */
        @Override
        public HashMap<Integer, Color> getEntranceStudentsIDs(String nickname){
            return players.getByNickname(nickname).getEntranceStudentsIDs();
        }

        /**
         * Method to get all the table IDs and their color
         * @return an HashMap with the table color as key and the Table ID as object
         */
        @Override
        public HashMap<Color, Integer> getTableIDs(String nickname){
            return players.getByNickname(nickname).getTableIDs();
        }

        /**
         * Method to observe all the students in a table
         * @param color The color of the table
         * @return List with the student IDs in the requested table
         */
        @Override
        public List<Integer> getTableStudentsIDs(String nickname, Color color){
            return players.getByNickname(nickname).getTableStudentsIDs(color);
        }

        /**
         * Returns the amount of towers contained in the TowerSpace of a given player
         * @param nickname the nickname of the player to check
         * @return the amount of towers contained in the TowerSpace
         */
        @Override
        public int getTowersLeft(String nickname){
            return players.getByNickname(nickname).getTowersLeft();
        }

            /**
             * Method used to observe which player chose which wizard
             * @return An HashMap containing the nickname of the Player and the Wizard chosen
             */
            public HashMap<String, WizardType> getPlayersWizardType(){
                HashMap<String, WizardType> result = new HashMap<>();
                for(Player player : players){
                    result.put(player.getNickname(), player.getWizardType());
                }
                return  result;
            }

        @Override
        public int getTableCoinsLeft(String nickname, Color color){
            return getPlayerByNickname(nickname).getCoinsLeft(color);
        }
            //endregion
        //region ProfessorSet
        /**
         * Method to observe which Professor is owned by who
         * @return An HashMap with the color of the professor as Key and its owner as Object (null if no player owns it)
         */
        @Override
        public HashMap<Color, String> getProfessorsOwner(){
            return  professorSet.getProfessorsOwner();
        }
        //endregion
        //region Clouds
        /**
         * Returns a list containing all the IDs of CloudTiles
         * @return a list containing all the IDs of CloudTiles
         */
        @Override
        public List<Integer> getCloudIDs(){
            List<Integer> cloudIDs = new ArrayList<>();
            for(CloudTile cloudTile : clouds){
                cloudIDs.add(cloudTile.getID());
            }
            return  cloudIDs;
        }

    /**
     * Return all the IDs of students contained in a given cloud along with their color
     * @param cloudTileID the ID of the CloudTile
     * @return an HashMap with the Student IDs as Key and their color as Object
     * @throws IllegalArgumentException thrown when the CloudTileID doesn't match any existing cloud
     */
    @Override
    public HashMap<Integer, Color> getCloudStudentsIDs(int cloudTileID) throws IllegalArgumentException{
        for(CloudTile cloudTile : clouds){
            if(cloudTile.getID() == cloudTileID)
                return cloudTile.getStudentIDsAndColor();
        }
        throw new IllegalArgumentException("No cloud with such ID exists");
    }
    //endregion
        //region Bag
        /**
         * Method to observe how many students are left in the abg
         * @return the number of students left in the bag
         */
        @Override
        public int getBagStudentsLeft(){//Probably not needed
            return  bag.getPawnIDs().size();
        }
        //endregion
        //region Archipelago
        /**
         * Returns all students contained in all islands with their color
         * @return An HashMap with StudentID as key and Color as value
         */
        @Override
        public HashMap<Integer, Color> getArchipelagoStudentIDs(){
            return archipelago.getStudentIDs();
        }

        /**
         * Searches all IslandTiles to find which students each contains
         * @return A HashMap containing as Key the ID of the IslandTile, as object a list of StudentIDs
         */
        @Override
        public HashMap<Integer, List<Integer>> getIslandTilesStudentsIDs(){
            return archipelago.getIslandTilesStudentsIDs();
        }

        /**
         * For each IslandGroup finds the IDs of its IslandTiles
         * @return An HashMap with key the (current) index of the IslandGroup and a list of its IslandTiles IDs
         */
        @Override
        public HashMap<Integer, List<Integer>> getIslandTilesIDs(){
            return  archipelago.getIslandTilesIDs();
        }

        /**
         * Returns the IslandGroup index of the IslandGroup which contains MotherNature
         * @return the IslandGroup index of the IslandGroup which contains MotherNature
         */
        @Override
        public int getMotherNatureIslandGroupIdx() {
            return archipelago.getMotherNatureIslandGroupIndex();
        }

        /**
         * Returns the IslandTile ID of the IslandTile which contains MotherNature
         * @return the IslandTile ID of the IslandTile which contains MotherNature
         */
        @Override
        public int getMotherNatureIslandTileID(){
            return archipelago.getMotherNatureIslandTileID();
        }

        /**
        * Returns the IslandGroups indexes along with the TowerColor of the Team who has towers.
        * The color is null when no Team holds the IslandGroup
        * @return an HashMap containing the indexes of the IslandGroup as key and the TowerColor as Key
        */
        @Override
        public HashMap<Integer, TowerColor> getIslandGroupsOwners(){
        return archipelago.getIslandGroupsOwner();
    }

        /**
         * Returns the IslandGroups indexes along with the number of NoEntryTiles each contains
         * @return The IslandGroups indexes along with the number of NoEntryTiles each contains
         */
        @Override
        public HashMap<Integer, Integer> getNoEntryTilesArchipelago(){
            return archipelago.getNoEntryTiles();
        }

        //endregion
        //region CharacterManager


        @Override
        public int getCoinsLeft() {
            return 0;
        }

        @Override
        public int getCoins(String nickname) {
            return 0;
        }

        @Override
        public List<Integer> getDrawnCharacterIDs() {
            return new ArrayList<>();
        }

        @Override
        public int getActiveCharacterID() {
            return -1;
        }

        @Override
        public int getActiveCharacterMaxUses() {
            return 0;
        }

        @Override
        public int getActiveCharacterUsesLeft() {
            return 0;
        }

        @Override
        public HashMap<Integer, Color> getCharacterStudents(int ID) {
            return new HashMap<>();
        }

        @Override
        public int getCharacterCost(int ID) {
            return 0;
        }

        @Override
        public boolean getCharacterOvercharge(int ID) {
            return false;
        }

        @Override
        public int getNoEntryTilesCharacter(int ID) {
            return 0;
        }

        @Override
        public List<RequestParameter> getCurrentRequestParameters() {
            return new ArrayList<>();
        }

        //endregion

    //endregion
}