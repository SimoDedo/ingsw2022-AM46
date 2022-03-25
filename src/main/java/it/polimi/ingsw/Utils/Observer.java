package it.polimi.ingsw.Utils;

/**
 * Interface for implementing the Observer/Observable pattern in the project.
 */
public interface Observer {
    /**
     * This method is called whenever the observed object is changed.
     * @param o the observable object
     * @param arg an argument passed to the notify method
     */
    void update(Observable o, Object arg);
}
