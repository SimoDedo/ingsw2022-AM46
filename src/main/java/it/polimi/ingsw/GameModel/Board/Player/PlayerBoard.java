package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.List;
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

    /**
     * Searches for a student in the entrance and dining room
     * @param ID of the student to return
     * @return student of specified ID
     * @throws NoSuchElementException if no student with this ID is found
     */
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
     * @throws IllegalStateException if the size of the student list is incorrect for the game type
     * @throws IllegalArgumentException if any of the students is already in the entrance
     */
    public void refillEntrance(List<Student> students) throws IllegalStateException, IllegalArgumentException {
        entrance.refillStudents(students);
    }


    public int getScore(Color c){
        return diningRoom.getScore(c);
    }

    public int getTowersPlaced(){
        return towerSpace.getTowersPlaced();
    }

    /**
     * @return a tower from the towerSpace
     * @throws GameOverException if the tower was the last
     */
    public Tower takeTower() throws GameOverException {
        return towerSpace.takeTower();
    }

    public void placeTower(Tower tower){
        towerSpace.placeTower(tower);
    }

    public Table getTable(Color color) {
        return diningRoom.getTable(color);
    }

    /**
     * Searches for a student in the entrance only
     * @param studentID of the student to return
     * @return student of specified ID
     */
    public Student getStudentFromEntrance(int studentID) {
        return entrance.getPawnByID(studentID);
    }


    /**
     * Removes a student from the player's board using its ID.
     * @param studentID the ID of the student to remove
     * @return the removed student
     * @throws NoSuchElementException if the student is not in the entrance, nor in the table of its color
     */
    public Student removeStudentByID(int studentID) throws NoSuchElementException {
        try {
            return entrance.removeStudentByID(studentID);
        } catch (Exception entranceException) {
            try {
                return diningRoom.removeStudentByID(studentID);
            }
            catch (Exception diningRoomException) {
                throw new NoSuchElementException("No student was found");
            }
        }
    }
}
