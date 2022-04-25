package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


public class DiningRoom {
    private final List<Table> tables;

    /**
     * creates an array with a table for each student color
     * @param player owner of this dining room
     */
    public DiningRoom(Player player){
        tables = new ArrayList<>();
        for(Color c : Color.values()){tables.add(new Table(player, 10, c));}
    }

    /**
     * @param color of the table
     * @return the table
     */
    public Table getTable(Color color){
        for(Table t : tables){
            if(t.getColor() == color) return t;
        }
        return null;
    }

    /**
     * @param color of the table
     * @return score of the table
     */
    public int getScore(Color color){
        for(Table t : tables){
            if(t.getColor() == color) return t.getScore();
        }
        return 0;
    }

    /**
     * @param student to place in the table of corresponding color
     * @throws FullTableException if the table of the same color of the student is full
     * @throws IllegalArgumentException if the student is already in a table, and the table is not full
     */
    public boolean placeStudent(Student student) throws FullTableException {
        boolean giveCoin = false;
        for(Color c : Color.values()){
            if(student.getColor() == c)
                giveCoin = getTable(c).placeStudent(student);
        }
        return giveCoin;
    }

    /**
     * @param ID ID of the Student to get
     * @return the student with the specified ID
     * @throws NoSuchElementException if this DiningRoom does not contain the specified student
     */
    public Student getStudentByID(int ID) throws NoSuchElementException {
        Student s = null;
        for(Table t : tables){
            if (s == null) s = t.getPawnByID(ID);
        }
        if (s == null) throw new NoSuchElementException("No student with the given ID in the dining room");
        return s;
    }

    public Student removeStudentByID (int ID) throws NoSuchElementException {
        Student student = getStudentByID(ID);
        getTable(student.getColor()).removePawn(student);
        student.setStudentContainer(null);
        return student;
    }

    /**
     * Method to get all the table IDs and their color
     * @return an HashMap with the table color as key and the Table ID as object
     */
    public HashMap<Color, Integer> getTableIDs(){
        HashMap<Color, Integer> result = new HashMap<>();
        for (Table table : tables){
            result.put(table.getColor(), table.getID());
        }
        return result;
    }

    /**
     * Method to observe all the students in a table
     * @param color The color of the table
     * @return List with the student IDs in the requested table
     */
    public List<Integer> getTableStudentsIDs(Color color){
        return getTable(color).getStudentIDsAndColor().keySet().stream().toList();
    }


}
