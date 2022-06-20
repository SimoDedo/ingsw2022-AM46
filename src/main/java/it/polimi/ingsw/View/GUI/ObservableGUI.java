package it.polimi.ingsw.View.GUI;

/**
 * Interface common to all the GUI components that can be observed.
 * Each observable can have an observer set so that it can be notified (through its methods) of an action taken.
 */
public interface ObservableGUI {

    /**
     * Sets the given observer to observe the GUI component.
     * @param observer the observer to set.
     */
    void setObserver(ObserverGUI observer);
}
