package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;

import java.util.HashMap;
import java.util.List;

/**
 * Interface common to all containers who place and remove students. Also offer observer methods.
 */
public interface PlaceAndRemoveStudent {

    void placePawn(Student student);

    boolean removePawn(Student studentToRemove);

    Student removePawnByID(int ID);

    Student getPawnByID(int ID);

    List<Integer> getPawnIDs();

    HashMap<Integer, Color> getStudentIDsAndColor();

}
