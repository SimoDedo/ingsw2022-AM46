package it.polimi.ingsw.Network.Client;
import java.beans.PropertyChangeListener;


/**
 * Interface used by the CLI and the GUI. An action handler will have to fire appropriate property changes.
 */

public interface UI extends PropertyChangeListener{

    void displayLogin();

    void displayBoard();

    void displayMessage(String message);

}
