package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class PlayerBoard {
    private TowerSpace towerSpace;
    private Entrance entrance;
    private DiningRoom diningRoom;
    private int studentsToPick;



    /**
     * Creates a new Entrance, TowerSpace and DiningRoom. The maximum amount of students the entrance can hold and the
     * maximum amount of students that can be moved from it every turn are decided based on the amount of players.
     * @param player the owner of the board
     * @param towerColor color of the player's towers
     * @param players number of players in the game
     * @param maxTowers maximum towers the towerSpace can hold. Can be 0 in the case of 4 players.
     * @param bag reference to Bag from which to draw students for the entrance
     */
    public PlayerBoard(Player player, TowerColor towerColor, int players, int maxTowers, Bag bag){
        switch (players){
            case 2: case 4:
                this.entrance = new Entrance(player, 7, 3, bag);
                this.studentsToPick = 3;
                break;
            case 3:
                this.entrance = new Entrance(player, 9, 4, bag);
                this.studentsToPick = 4;
                break;
        }
        this.towerSpace = new TowerSpace(player, maxTowers, towerColor);
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
    public void refillEntrance(List<Student> students) throws IllegalArgumentException{
        entrance.refillStudents(students);
    }


    public int getScore(Color c){
        return diningRoom.getScore(c);
    }

    public int getTowersPlaced(){
        return towerSpace.getTowersPlaced();
    }

    /**
     * @param studentDestinations HashMap<Student ID><BoardPiece ID> (their destinations).
     *                            If the destination ID is set to 0 they are to be placed in the diningRoom.
     * @return HashMap of Students and the corresponding destination island's ID. Movements to Table locations are
     * resolved here
     * @throws IllegalArgumentException if the hashmap is not sized correctly
     * @throws NoSuchElementException if any of the students cannot be found. If there are no matches with the
     *      * boardPiece IDs it will be assumed they are all Island locations.
     */
    public HashMap<Student, Integer> moveStudentsFromEntranceToDN(HashMap<Integer, Integer> studentDestinations)
            throws IllegalArgumentException, NoSuchElementException{
        HashMap<Student, Integer> islandMovements = new HashMap<>();
        for(Map.Entry<Integer, Integer> pair : studentDestinations.entrySet()){
            Student student = entrance.getPawnByID(pair.getKey());

            // this requires that no island has ID 0... maybe a custom ID class to handle special values?
            if(pair.getValue() == 0){
                entrance.removePawn(student);
                diningRoom.getTable(student.getColor()).placeStudent(student);
            } else {
                islandMovements.put(student, pair.getValue());
            }
        }
        return islandMovements;
    }



}
