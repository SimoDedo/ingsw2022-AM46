package it.polimi.ingsw.Utils.Enum;

/**
 * Utility function for managing turns and phases in TurnManager.
 */
public enum Phase {
    PLANNING,
    ACTION,
    IDLE // placeholder phase, ideally never shown -- could be used by client to know if it's waiting on another player's turn
}
