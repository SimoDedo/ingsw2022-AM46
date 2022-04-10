package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CloudTile;
import it.polimi.ingsw.GameModel.Board.Player.*;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.*;

/**
 * Main class of the GameModel. This class acts as a hotpoint for the classes below it, that deal
 * with board pieces and pawns. It's also the interface to which the controller communicates and
 * calls methods. Finally, it coordinates game actions between the main components of the Model:
 * the Clouds, the Bag and the Coin Bag; the ProfessorSet storing Professors; the Archipelago storing
 * islands and Mother nature; the Players owning a board and a deck; the CharacterManager storing and
 * activating Characters; the TurnManager that decides the turn order and the current player.
 */
public class Game {

    private final Player neutralPlayer = new Player();
    private Bag bag = new Bag();
    private Archipelago archipelago = new Archipelago();
    private List<CloudTile> clouds = new ArrayList<>();
    private ProfessorSet professorSet = new ProfessorSet();
    private PlayerList players = new PlayerList();
    private TurnManager turnManager = new TurnManager();
    private TeamManager teamManager = new TeamManager();

    /**
     * Linked hashmap that stores the Assistant cards played this round, and by whom they were played.
     */
    private Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();

    /**
     * Constructor for Game. Places 10 students across the Archipelago, fills the bag with the
     * remaining students, then sets up the number and size of clouds and of players. Finally adds
     * the Players to the TurnManager.
     * @param gameConfig the game configuration object, created by the GameFactory
     * @param teamComposition the nickname and tower color that every player chose during match setup
     */
    public Game(GameConfig gameConfig, LinkedHashMap<String, TowerColor> teamComposition) {
        archipelago.initialStudentPlacement(bag.drawN(10));
        bag.fillRemaining();
        gameConfig.getPlayerConfig().setBag(bag);

        for (int i = 0; i < gameConfig.getNumOfClouds(); i++)
            clouds.add(new CloudTile(gameConfig.getCloudSize(), bag));

        players = teamManager.create(gameConfig, teamComposition);
        for(Player player : players)
            turnManager.addPlayerClockwise(player);
    }

    /**
     * Getter for a Player by their nickname.
     * @param nickname nickname of the player to find
     * @return the player with the given nickname if found
     * @throws NoSuchElementException when no player in the game has the given nickname
     */
    private Player getPlayerByNickname(String nickname) throws NoSuchElementException { //todo: re-implement using playerList methods
        Player playerToReturn = null;
        for (Player player : players) {
            if(player.getNickname().equals(nickname))
                playerToReturn = player;
        }
        if (playerToReturn == null) throw new NoSuchElementException("Player not found");
        return playerToReturn;
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
     * Method that determines the planning order of the very first round, and actually starts the game
     * (game phase goes from idle to planning)
     */
    public void determineFirstRoundOrder() {
        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // set from idle -> planning
    }

    /**
     * Getter for the current player in the game.
     * @return the player currently executing their planning/action turn
     */
    public String getCurrentPlayer() {
        return turnManager.getCurrentPlayer().getNickname();
    } // could be useful to controller

    /**
     * Method that retrieves the played AssistantCard by its ID and passes its turn order to TurnManager.
     * It also checks if a Player is "desperate", that is, if they can only play Assistants that
     * have already been played by someone else.
     * @param nickname the nickname of the Player who is playing their Assistant
     * @param assistantID the ID of the AssistantCard chosen
     * @throws IllegalArgumentException if a player is not "desperate" and chooses an Assistant card already chosen by someone else
     */
    public void playAssistant(String nickname, int assistantID) throws IllegalArgumentException {
        if (!checkDesperate(nickname)) {
            for (AssistantCard assistantCard : cardsPlayedThisRound.values()) {
                if (assistantCard.getID() == assistantID)
                    throw new IllegalArgumentException("Assistant card already chosen");
            }
        }
        AssistantCard assistantPlayed = getPlayerByNickname(nickname).playAssistant(assistantID); // doesn't treat desperate
        cardsPlayedThisRound.put(getPlayerByNickname(nickname), assistantPlayed);
    }

    /**
     * Method that checks if a certain Player is "desperate", that is, if they can only play
     * Assistants that have already been played by someone else.
     * @param nickname the Player to check
     * @return true if the Player is "desperate", false otherwise
     */
    private boolean checkDesperate(String nickname) {
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
    public void moveStudentFromEntrance(String nickname, int studentID, int containerID) {
        Player player = getPlayerByNickname(nickname);
        Student student = player.getStudentFromEntrance(studentID); // will have to throw exception if not present
        Table potentialTable = player.getTable(student.getColor());
        if (potentialTable.getID() == containerID) potentialTable.moveStudent(student);
        else {
            archipelago.placeStudent(student, archipelago.getIslandTileByID(containerID)); // will have to throw exception
        }
        checkAndMoveProfessor(student.getColor());
    }

    /**
     * Method that checks if any Player has gained the right to host the Professor of the given color
     * by moving students into their dining room.
     * @param color the color of the Professor to check
     */
    public void checkAndMoveProfessor(Color color) {
        professorSet.checkAndMoveProfessor(color); // this in turn calls a checkAndMoveProfessor strategy
    }

    /**
     * Method that moves mother nature on the chosen island, provided the current player's assistant
     * card has a move power equal or greater than the number of hops required for mother nature to
     * land on that island tile. Should throw an exception
     * @param nickname
     * @param islandTileID
     */
    public void moveMotherNature(String nickname, int islandTileID) {
    } //todo: receives movecount from cardsplayedthisround, calls archipelago.movemothernature(islandtileid, movecount). finally calls resolveislandgroup

    /**
     * Method that "resolves" an island group, that is, it determines what player has the most
     * influence on that group, places one (or more) of that player's towers on the group if necessary
     * and finally checks if a merge is possible with the groups on the right or left of that group.
     * @param islandGroup the group to resolve
     * @throws GameOverException if the total number of groups in the archipelago at the end of the process is 3 (or less)
     */
    public void resolveIslandGroup(IslandGroup islandGroup) throws GameOverException {
        archipelago.resolveIslandGroup(islandGroup, players, professorSet);
    }

    /**
     * Method to take all students from a CloudTile and place them in the current player's entrance.
     * @param nickname the nickname of the player who is taking the students from the cloud
     * @param cloudID the ID of the cloud that the player chose
     */
    public void takeFromCloud(String nickname, int cloudID) {
        for (CloudTile cloud : clouds) {
            if (cloud.getID() == cloudID) {
                List<Student> studentsTaken = cloud.removeAll();
                getPlayerByNickname(nickname).addToEntrance(studentsTaken);
            }
        }
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
     * Method that moves the Assistant cards played this round into their respective discard pile.
     */
    public void pushThisRoundInLastRound() {

    }
}
