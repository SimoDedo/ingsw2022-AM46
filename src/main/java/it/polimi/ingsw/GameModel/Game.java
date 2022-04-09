package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CloudTile;
import it.polimi.ingsw.GameModel.Board.Player.*;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.*;

public class Game {

    private final Player neutralPlayer = new Player();
    private Bag bag = new Bag();
    private Archipelago archipelago = new Archipelago();
    private List<CloudTile> clouds = new ArrayList<>();
    private ProfessorSet professorSet = new ProfessorSet();
    private PlayerList players = new PlayerList();
    private TurnManager turnManager = new TurnManager();
    private TeamManager teamManager = new TeamManager();
    private Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();

    public Game(GameConfig gameConfig, LinkedHashMap<String, TowerColor> teamComposition) {
        archipelago.initialStudentPlacement(bag.drawN(10));
        bag.fillRemaining();
        gameConfig.getPlayerConfig().setBag(bag);

        for (int i = 0; i < gameConfig.getNumOfClouds(); i++)
            clouds.add(new CloudTile(gameConfig.getCloudSize(), bag));

        // the following code can be (almost, except for the TurnManager part) moved to an ad-hoc class: TeamManager. <- moved in TeamManager cry about it
        players = teamManager.create(gameConfig, teamComposition);
        for(Player player : players)
            turnManager.addPlayerClockwise(player);
    }


    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        Player playerToReturn = null;
        for (Player player : players) {
            if(player.getNickname().equals(nickname))
                playerToReturn = player;
        }
        if (playerToReturn == null) throw new NoSuchElementException("Player not found");
        return playerToReturn;
    }

    private List<WizardType> getWizardTypes() { // can be made using streams
        List<WizardType> wizardTypes = new ArrayList<>();
        for (Player player : players) {
                wizardTypes.add(player.getWizardType());
        }
        return wizardTypes;
    }

    public void assignWizard(String nickname, WizardType wizardType) throws NoSuchElementException {
        if (getWizardTypes().contains(wizardType)) throw new IllegalArgumentException("Wizard type already chosen");
        Player player = getPlayerByNickname(nickname);
        player.pickWizard(wizardType);
    }

    public void determineFirstRoundOrder() {
        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // set from idle -> planning
    }

    public String getCurrentPlayer() {
        return turnManager.getCurrentPlayer().getNickname();
    } // could be useful to controller


    /**
     * @param nickname of the player playing the assistant
     * @param assistantID ID of the assistant being played
     * @throws IllegalArgumentException if the assistant is not in the player's hand
     * @throws LastRoundException if the hand of the player is empty after playing the assistant
     */
    public void playAssistant(String nickname, int assistantID) throws IllegalArgumentException, LastRoundException {
        if (!checkDesperate(nickname)) {
            for (AssistantCard assistantCard : cardsPlayedThisRound.values()) {
                if (assistantCard.getID() == assistantID)
                    throw new IllegalArgumentException("Assistant card already chosen");
            }
        }
        AssistantCard assistantPlayed = getPlayerByNickname(nickname).playAssistant(assistantID); // doesn't treat desperate
        cardsPlayedThisRound.put(getPlayerByNickname(nickname), assistantPlayed);
        if(getPlayerByNickname(nickname).getDeck().size() == 0){ throw new LastRoundException(); }
    }

    private boolean checkDesperate(String nickname) {
        return getPlayerByNickname(nickname).checkDesperate(cardsPlayedThisRound.values());
    }

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

    public void checkAndMoveProfessor(Color color) {
    } //this calls a strategy! todo: calls professorset.setowner when winner is found

    public void moveMotherNature(String nickname, int islandTileID) {
    } //todo: receives movecount from cardsplayedthisround, calls archipelago.movemothernature(islandtileid, movecount). finally calls resolveislandgroup


    // should we be passing GameOverException on to the controller... thoughts? prayers? lmk
    public void resolveIslandGroup(IslandGroup islandGroup) throws GameOverException{
        archipelago.resolveIslandGroup(islandGroup, players, professorSet);
    }

    /**
     * @param nickname of the player who has picked the cloud and is about to receive its students
     * @param cloudID the ID of the cloud containing the students. If the cloud in question is not selectable
     *                because it has already been picked or because this is the last round and the bag is empty nothing happens.
     */
    public void takeFromCloud(String nickname, int cloudID) {
        for (CloudTile cloud : clouds) {
            if (cloud.getID() == cloudID) {
                if(cloud.isSelectable()) {
                    List<Student> studentsTaken = cloud.removeAll();
                    getPlayerByNickname(nickname).refillEntrance(studentsTaken);
                }
            }
        }
    }

    public void determinePlanningOrder() {
        turnManager.determinePlanningOrder();
    }

    public void determineActionOrder() {
        turnManager.determineActionOrder(cardsPlayedThisRound);
    }

    public void pushThisRoundInLastRound() {}


    public void refillClouds() {
        try{
            for(CloudTile c : clouds){ c.fill(); }
        } catch (LastRoundException e){
            for(CloudTile c : clouds){ c.removeAll(); }
        }
    }

    /**
     * checks if during the last round the students where exhausted or any player has finished their cards
     */
    // for now only useful to end game if the students from the bag where exhausted this round, or
    // the players have played all their assistant cards.
    public void endOfRoundOperations(){
        // if any player has 0 cards at the end of the round the game ends
        // checking all is a bit pedantic but whatever
        for(Player p : players){
            if(p.getDeck().size() == 0){ determineWinner(); }
        }
        if(bag.pawnCount() == 0) { determineWinner(); }
    }

    private void determineWinner(){
        Player currentWinner = players.get(0);

        for(Player p : players.getTowerHolders()) {
            if(p.getTowersPlaced() > currentWinner.getTowersPlaced()){
                currentWinner = p;
            } else if(p.getTowersPlaced() == currentWinner.getTowersPlaced()){
                currentWinner = professorSet.determineStrongestPlayer(p, currentWinner);
            }
        }
        TowerColor actualWinner = currentWinner.getTowerColor();
        System.exit(0); // :o
    }
}
