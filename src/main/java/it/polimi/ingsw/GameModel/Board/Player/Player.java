package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.*;

public class Player {

    private final String nickname;
    private final PlayerBoard playerBoard;
    private Wizard wizard;
    private final boolean isTowerHolder;

    private TowerColor towerColor;

    private int coins;

    public Player(String nickname, TowerColor towerColor, boolean isTowerHolder, PlayerConfig playerConfig) {
        this.nickname = nickname;
        this.isTowerHolder = isTowerHolder;
        this.towerColor = towerColor;
        playerBoard = new PlayerBoard(this, towerColor, isTowerHolder, playerConfig);
    }

    /**
     * Constructor for neutral player (i.e. island and bag owner)... there is  probably a better way to do it?
     */
    public Player() {
        nickname = "NEUTRAL";
        playerBoard = null;
        isTowerHolder = false;
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
    public AssistantCard playAssistant(int assistantID) throws NoSuchElementException {
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

    public int getTowersPlaced() {
        return playerBoard.getTowersPlaced();
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    /**
     * @return a tower taken from the playerBoard's tower space
     * @throws GameOverException if the last tower is taken
     */
    public Tower takeTower(){ return playerBoard.takeTower(); }

    /**
     * puts a tower back to the tower space
     * @param tower removed from an island conquered by another player
     */
    public void placeTower(Tower tower) {
        playerBoard.placeTower(tower);
    }

    public WizardType getWizardType() { return wizard == null ? null : wizard.getType(); }

    /**
     * @return true if this player holds towers in his playerBoard
     */
    public boolean isTowerHolder() { return isTowerHolder; }

    /**
     * @return the list of assistant cards
     */
    public List<AssistantCard> getDeck() {
        return wizard.getDeck();
    }

    /**
     * Method that checks if a player is "desperate", that is, if they can only play assistants that
     * have already been played by someone else in this round.
     *
     * @param cardsPlayedThisRound Collection of all the assistants played this round
     * @return true if the player only has cards which have already been played this round.
     */
    public boolean checkDesperate(Collection<AssistantCard> cardsPlayedThisRound) {
        for (AssistantCard cardInHand : getDeck()) {
            int turnOrder = cardInHand.getTurnOrder();
            if (cardsPlayedThisRound.stream().noneMatch(card -> card.getTurnOrder() == turnOrder)) return false;
        }
        return true;
    }

    public Table getTable(Color color) {
        return playerBoard.getTable(color);
    }

    /**
     * Method that looks for the student with the specific ID in the player's entrance.
     * @param studentID the ID of the student to return
     * @return the student
     */
    public Student getStudentFromEntrance(int studentID) {
        return playerBoard.getStudentFromEntrance(studentID);
    }

    /**
     * Method for bulk-adding students to a player's entrance. This method is called when an entrance
     * is being filled with students from a cloud.
     *
     * @param studentsFromCloud the students to add to the entrance
     */
    public void addToEntrance(List<Student> studentsFromCloud) {
        for (Student student : studentsFromCloud) addToEntrance(student);
    }

    /**
     * Method that adds a student to the player's entrance. This method is called by characters, or when
     * filling the entrance with students coming from a cloud.
     *
     * @param student the student to add to the entrance
     */
    public void addToEntrance(Student student) {
        playerBoard.addToEntrance(student);
    }

    /**
     * Method that removes a student from the player's entrance/dining room. This method should only
     * be called by characters.
     *
     * @param studentID the ID of the student to remove
     * @return the student removed from the entrance
     */
    public Student removeStudentByID(int studentID) {
        return playerBoard.removeStudentByID(studentID);
    }

    /**
     * Method that adds a student to its respective dining room in the player's board.
     *
     * @param student the student to place on the dining room
     */
    public void addToDR(Student student) throws FullTableException {
        playerBoard.getTable(student.getColor()).placeStudent(student);
    }

    /**
     * Method that removes three students of a given color from the player's dining room. It is only
     * called by Characeter 12.
     *
     * @param color the color of the three students to remove from the dining room
     * @return the
     */
    public List<Student> removeNFromDR(int numberToRemove, Color color) {
        List<Student> removedStudents = new ArrayList<>();
        for (int i = 0; i < numberToRemove; i++) {
            try { removedStudents.add(playerBoard.getTable(color).removePawn()); }
            catch (IndexOutOfBoundsException ioobe) { break; }
        }
        return removedStudents;
    }

    //region State observer method

    /**
     * Returns a list of cards that weren't yet played (thus to be shown to the player)
     * @return a list of cards IDs
     */
    public List<Integer> getCardsLeft(){
        return wizard.getCardsLeft();
    }

    /**
     * Method to observe all the students in the entrance and their color
     * @return HashMap with the student ID as key and its color as object
     */
    public HashMap<Integer, Color> getEntranceStudentsIDs(){
        return playerBoard.getEntranceStudentsIDs();
    }

    /**
     * Method to get all the table IDs and their color
     * @return an HashMap with the table color as key and the Table ID as object
     */
    public HashMap<Color, Integer> getTableIDs(){
        return playerBoard.getTableIDs();
    }

    /**
     * Method to observe all the students in a table
     * @param color The color of the table
     * @return List with the student IDs in the requested table
     */
    public List<Integer> getTableStudentsIDs(Color color){
        return playerBoard.getTableStudentsIDs(color);
    }

    /**
     * Returns the amount of towers contained in the TowerSpace
     * @return the amount of towers contained in the TowerSpace
     */
    public int getTowersLeft(){
        return playerBoard.getTowersLeft();
    }

    //endregion

}
