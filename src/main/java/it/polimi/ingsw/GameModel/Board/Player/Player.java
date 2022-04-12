package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Player {
    private final String nickname;
    private final PlayerBoard playerBoard;
    private Wizard wizard;
    private final boolean isTowerHolder;

    private TowerColor towerColor;

    private final boolean isNeutral;
    private int coins;


    public Player(String nickname, TowerColor towerColor, boolean isTowerHolder, PlayerConfig playerConfig) {
        this.nickname = nickname;
        this.isTowerHolder = isTowerHolder;
        this.isNeutral = false;
        this.towerColor = towerColor;
        playerBoard = new PlayerBoard(this, towerColor, isTowerHolder, playerConfig);
    }

    /**
     * Constructor for neutral player (i.e. island and bag owner)... there is  probably a better way to do it?
     */
    public Player() {
        nickname = null;
        playerBoard = null;
        isTowerHolder = false;
        isNeutral = true;
    }

    /**
     * creates a deck of assistant cards
     * @param wizardType type of card back
     */
    public void pickWizard(WizardType wizardType){
        this.wizard = new Wizard(wizardType);
    }

    /**
     * @param assistantID unique ID of the assistant to play
     * @return the assistant which has been removed from the deck
     */
    public AssistantCard playAssistant(int assistantID) {
        return wizard.playAssistant(assistantID);
    }

    /**
     * @return the unique identifier for this player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * adds 1 to coin count
     */
    public void awardCoin(){
        coins++;
    }

    /**
     * @param color of the students
     * @return amount of students of the specified color in the dining room
     */
    public int getScore(Color color){
        // assert playerBoard != null;
        return playerBoard.getScore(color);
    }

    public int getCoins() {
        return coins;
    }

    public int getTowersPlaced() { return playerBoard.getTowersPlaced(); }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    /**
     * @return a tower taken from the playerBoard's tower space
     * @throws GameOverException if the last tower is taken
     */
    public Tower takeTower() throws GameOverException { return playerBoard.takeTower(); }

    /**
     * puts a tower back to the tower space
     * @param tower removed from an island conquered by another player
     */
    public void placeTower(Tower tower) {
        playerBoard.placeTower(tower);
    }

    public WizardType getWizardType() { return wizard.getType(); }

    /**
     * Searches for a student in the entrance and dining room
     * @param ID of the student to search
     * @return the student of specified ID
     * @throws NoSuchElementException if no such student is in the playerBoard
     */
    public Student getStudentByID(int ID) throws NoSuchElementException { return playerBoard.getStudentByID(ID); }

    /**
     * @return true if this player holds towers in his playerBoard
     */
    public boolean isTowerHolder() { return isTowerHolder; }

    /**
     * @param students list of students to put into the entrance at the beginning of the round
     * @throws IllegalStateException if the list is of wrong size, or if any of the students we want to place are
     * already in the entrance
     * @throws IllegalArgumentException if any of the students is already in the entrance
     */
    public void refillEntrance(List<Student> students) throws IllegalStateException, IllegalArgumentException{
        //assert playerBoard != null;
        playerBoard.refillEntrance(students);
    }


    /**
     * @return the list of assistant cards
     */
    public List<AssistantCard> getDeck() {
        return wizard.getDeck();
    }

    /**
     * @param cardsPlayedThisRound Collection of all the assistants played this round
     * @return true if the player only has cards which have already been played this round.
     */
    public boolean checkDesperate(Collection<AssistantCard> cardsPlayedThisRound) {
        for (AssistantCard cardInHand : getDeck()) {
            int id = cardInHand.getID();
            if (cardsPlayedThisRound.stream().anyMatch(x -> x.getID() == id)) {continue;}
            return false;
        }
        return true;
    }

    public Table getTable(Color color) {
        return playerBoard.getTable(color);
    }

    /**
     * looks for student with specific id in the entrance
     * @param studentID the ID of the student to return
     * @return the student
     */
    public Student getStudentFromEntrance(int studentID) {
        return playerBoard.getStudentFromEntrance(studentID);
    }

}
