package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;

import java.util.ArrayList;

/**
 * Element of the board that serves as a student "buffer". The player's Entrance is filled using
 * CloudTiles.
 */
public class CloudTile extends StudentContainer {

    /**
     *
     */
    private final Bag bag;

    /**
     * Constructor for the Cloud class
     * @param maxCloudStudents max number of students on cloud
     * @param bag reference to Bag for drawing students autonomously
     */
    public CloudTile(int maxCloudStudents, Bag bag) {
        super(null, maxCloudStudents);
        this.bag = bag;
    }

    public void fill() {
        bag.drawN(getMaxPawns());
    }

    public ArrayList<Student> removeAll() {
        ArrayList<Student> drawnStudents = new ArrayList<>();
        for (int i = 0; i < getMaxPawns(); i++) {
            drawnStudents.add(removePawnByIndex(0));
        }
        return drawnStudents;
    }

}
