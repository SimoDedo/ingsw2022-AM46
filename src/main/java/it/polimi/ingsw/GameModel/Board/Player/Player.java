package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.BoardPiece;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Player {
    private final String nickname;
    private final PlayerBoard board;
    private Wizard wizard;
    private final boolean isTowerHolder;
    private final boolean isNeutral;

    private int coins;


    public Player(String nickname, int maxTowers, TowerColor towerColor, int players, List<Student> initialEntranceStudents){
        this.nickname = nickname;
        this.isTowerHolder = (maxTowers != 0);
        this.isNeutral = false;

        board = new PlayerBoard(this, towerColor, players, maxTowers, initialEntranceStudents);



    }

    /**
     * Constructor for neutral player (i.e. island and bag owner)... there is  probably a better way to do it?
     */
    public Player(){
        nickname = null;
        board = null;
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
        // assert board != null;
        return board.getScore(color);
    }

    public int getCoins() {
        return coins;
    }

    public int getTowersPlaced() throws NullPointerException { return board.getTowersPlaced(); }

    public WizardType getWizardType(){ return wizard.getType(); }

    public Student getStudentByID(int ID) throws NoSuchElementException { return board.getStudentByID(ID); }

    public boolean isTowerHolder(){ return isTowerHolder; }

    public void refillEntrance(List<Student> students) throws IllegalArgumentException{
        //assert board != null;
        board.refillEntrance(students);
    }

    public HashMap<Student, Integer> moveStudentsFromEntranceToDN(HashMap<Integer, Integer> studentDestinations)
            throws IllegalArgumentException, NoSuchElementException{
        //assert board != null;
        return board.moveStudentsFromEntranceToDN(studentDestinations);
    }

    public Tower takeTower() {
        return  null;
    }

    public void putTower(Tower towerRemoved) {
    }
}
