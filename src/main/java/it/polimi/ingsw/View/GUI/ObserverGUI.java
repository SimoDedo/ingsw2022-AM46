package it.polimi.ingsw.View.GUI;

/**
 * Interface that defines all the methods that could be called whenever the GUI receives an input.
 * A class that implements this interface is a class that can observe inputs given on the GUI and act accordingly.
 */
public interface ObserverGUI {

    /**
     * Method called to notify this observer that an IP was chosen.
     */
    void notifyIP();

    /**
     * Method called to notify this observer that a nickname was chosen.
     */
    void notifyNickname();

    /**
     * Method called to notify this observer that the game settings were chosen.
     */
    void notifyGameSettings();

    /**
     * Method called to notify this observer that a tower color was chosen.
     */
    void notifyTowerColor();

    /**
     * Method called to notify this observer that a wizard type was chosen.
     */
    void notifyWizardType();

    /**
     * Method called to notify this observer that a tower color was chosen.
     */
    void notifyAssistantCard();

    /**
     * Method called to notify this observer that a student from the entrance was chosen.
     */
    void notifyStudentEntrance();

    /**
     * Method called to notify this observer that a student from the dining room was chosen.
     */
    void notifyStudentDR();

    /**
     * Method called to notify this observer that a student from a character was chosen.
     */
    void notifyStudentChar();

    /**
     * Method called to notify this observer that a cloud was chosen.
     */
    void notifyCloud();

    /**
     * Method called to notify this observer that an island was chosen.
     */
    void notifyIsland();

    /**
     * Method called to notify this observer that a character was chosen.
     */
    void notifyCharacter();


    /**
     * Method called to notify this observer that a table was chosen.
     */
    void notifyTable();


    /**
     * Method called to notify this observer that a color was chosen.
     */
    void notifyColorChar();


    /**
     * Method called to notify this observer that an ability is being used.
     */
    void notifyAbility();

    /**
     * Method called to notify this observer that the turn was ended.
     */
    void notifyEndTurn();

    /**
     * Method called to notify this observer that the game has to be closed.
     */
    void notifyClose();

}
