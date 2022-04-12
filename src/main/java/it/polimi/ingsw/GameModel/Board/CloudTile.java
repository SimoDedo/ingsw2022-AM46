package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Element of the board that serves as a student "buffer". The player's Entrance is filled using
 * CloudTiles.
 */
public class CloudTile extends StudentContainer {

    /**
     *
     */
    private final Bag bag;
    private boolean selectable;

    /**
     * Constructor for the Cloud class
     * @param maxCloudStudents max number of students on cloud
     * @param bag reference to Bag for drawing students autonomously
     */
    public CloudTile(int maxCloudStudents, Bag bag) {
        super(null, maxCloudStudents);
        this.bag = bag;
    }

    public void fill() throws LastRoundException {
        List<Student> students = bag.drawN(this.getMaxPawns());
        if (students == null) { throw new LastRoundException(); }
        placePawns(students);
        selectable = true;
    }

    public List<Student> removeAll(){
        if(!selectable){return null;}
        selectable = false;
        List<Student> drawnStudents = new ArrayList<>();
        for (int i = 0; i < getMaxPawns(); i++) {
            drawnStudents.add(removePawn());
        }
        return drawnStudents;
    }

    /**
     * @return true if the island can be picked by a player
     */
    public boolean isSelectable(){
        return selectable;
    }

}
