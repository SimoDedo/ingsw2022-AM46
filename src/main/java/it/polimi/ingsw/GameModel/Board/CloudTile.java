package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;

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
        placePawns(bag.drawN(this.getMaxPawns()));
    }

    public List<Student> removeAll() throws IllegalStateException{
        if(pawnCount() == 0)
            throw new IllegalStateException("Island is already empty");
        List<Student> drawnStudents = new ArrayList<>();
        for (int i = 0; i < getMaxPawns(); i++) {
            drawnStudents.add(removePawnByIndex(0));
        }
        return drawnStudents;
    }

}
