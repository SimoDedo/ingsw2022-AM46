package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class DiningRoom {
    private List<Table> tables = new ArrayList<>();

    public DiningRoom(Player player){
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

    public void placeStudent(Student student) throws FullTableException {
        for(Color c : Color.values()){
            if(student.getColor() == c) getTable(c).placeStudent(student);
        }
    }

    /**
     * @param ID ID of the Student to get
     * @return the student with the specified ID
     * @throws NoSuchElementException if this DiningRoom does not contain the specified student
     */
    public Student getStudentByID(int ID) throws NoSuchElementException {
        Student s = null;
        for(Table t : tables){
            try {
                s = t.getPawnByID(ID);
                break;
            } catch (NoSuchElementException e){
                // empty catch body?
                e.printStackTrace();
            }
        }
        if(s == null){ throw new NoSuchElementException(); }
        return s;
    }



}
