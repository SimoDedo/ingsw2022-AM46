package it.polimi.ingsw.Utils;
import java.util.*;

/**
 * Interface for implementing the Observer/Observable pattern in the project.
 */
public interface Observable {
    void attach(Observer o);
    void detach(Observer o);
    void notify(Object arg);
}
