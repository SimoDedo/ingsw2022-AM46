package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Player {
    private final String nickname;
    private final PlayerBoard playerBoard;
    private Wizard wizard;
    private final boolean isTowerHolder;
    private final boolean isNeutral;

    private int coins;


    public Player(String nickname, TowerColor towerColor, boolean isTowerHolder, PlayerConfig playerConfig) {
        this.nickname = nickname;
        this.isTowerHolder = isTowerHolder;
        this.isNeutral = false;
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

    public void pickWizard(WizardType wizardType){
        this.wizard = new Wizard(wizardType);
    }

    public AssistantCard playAssistant(int assistantID){
        return wizard.playAssistant(assistantID);
    }

    public String getNickname() {
        return nickname;
    }

    public void awardCoin(){
        coins++;
    }

    public int getScore(Color color){
        // assert playerBoard != null;
        return playerBoard.getScore(color);
    }

    public int getCoins() {
        return coins;
    }

    public int getTowersPlaced() throws NullPointerException { return playerBoard.getTowersPlaced(); }

    public WizardType getWizardType(){ return wizard.getType(); }

    public Student getStudentByID(int ID) throws NoSuchElementException { return playerBoard.getStudentByID(ID); }

    public boolean isTowerHolder(){ return isTowerHolder; }

    public void refillEntrance(List<Student> students) throws IllegalArgumentException{
        //assert playerBoard != null;
        playerBoard.refillEntrance(students);
    }

    public HashMap<Student, Integer> moveStudentsFromEntranceToDN(HashMap<Integer, Integer> studentDestinations)
            throws IllegalArgumentException, NoSuchElementException{
        //assert playerBoard != null;
        return playerBoard.moveStudentsFromEntranceToDN(studentDestinations);
    }

    public Tower takeTower() {
        return  null;
    }

    public void putTower(Tower towerRemoved) {
    }
}
