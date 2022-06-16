package it.polimi.ingsw.View.GUI;

public interface ObserverGUI {

    void notifyIP();

    void notifyNickname();

    void notifyGameSettings();

    void notifyTowerColor();

    void notifyWizardType();

    /**
     * Notifies gui that a card has been chosen. Used by assistant cards.
     */
    void notifyAssistantCard();

    void notifyStudentEntrance();

    void notifyStudentDR();

    void notifyStudentChar();

    void notifyCloud();

    void notifyIsland();

    void notifyCharacter();

    void notifyTable();

    void notifyColorChar();

    void notifyAbility();

    void notifyEndTurn();

    void notifyClose();

}
