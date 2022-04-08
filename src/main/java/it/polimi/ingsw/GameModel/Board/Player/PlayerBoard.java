package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class PlayerBoard {
    private TowerSpace towerSpace = null;
    private Entrance entrance;
    private DiningRoom diningRoom;



    /**
     * Creates a new Entrance, TowerSpace and DiningRoom. The maximum amount of students the entrance can hold and the
     * maximum amount of students that can be moved from it every turn are decided based on the amount of players.
     * @param player the owner of the board
     * @param towerColor color of the player's towers
     */
    public PlayerBoard(Player player, TowerColor towerColor, boolean isTowerHolder, PlayerConfig playerConfig) {
        this.entrance = new Entrance(player, playerConfig.getInitialEntranceSize(), playerConfig.getMovableEntranceStudents(), playerConfig.getBag());
        if (isTowerHolder) this.towerSpace = new TowerSpace(player, playerConfig.getMaxTowers(), towerColor);
        this.diningRoom = new DiningRoom(player);
    }

    public Student getStudentByID(int ID) throws NoSuchElementException{
        Student student;
        try{
            student = entrance.getPawnByID(ID);
        } catch (NoSuchElementException e){
            student = diningRoom.getStudentByID(ID);
        }
        return student;
    }

    /**
     * @param students to place in the entrance
     * @throws IllegalArgumentException if the size of the student list is incorrect for the game type
     */
    public void refillEntrance(List<Student> students) throws IllegalStateException{
        entrance.refillStudents(students);
    }


    public int getScore(Color c){
        return diningRoom.getScore(c);
    }

    public int getTowersPlaced(){
        return towerSpace.getTowersPlaced();
    }

    public Tower takeTower() throws GameOverException {
        return towerSpace.takeTower();
    }

    public void placeTower(Tower tower){
        towerSpace.placeTower(tower);
    }

    public Table getTable(Color color) {
        return diningRoom.getTable(color);
    }

    public Student getStudentFromEntrance(int studentID) {
        return entrance.getPawnByID(studentID);
    }


    public Student removeStudentByID(int studentID) {
        Student student;
        try{
            student = entrance.getPawnByID(studentID);
            return entrance.removePawn(student);
        } catch (Exception ee) {
            try {
                student = diningRoom.getStudentByID(studentID);
                return diningRoom.getTable(student.getColor()).removePawn(student);
            }
            catch (Exception dne) {
                throw new NoSuchElementException("No student was found");
            }
        }
    }
}
