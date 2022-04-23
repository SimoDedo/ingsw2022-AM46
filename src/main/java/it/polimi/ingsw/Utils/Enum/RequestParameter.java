package it.polimi.ingsw.Utils.Enum;

/**
 * Enum for the parameters requested by the server/controller to the client.
 */
public enum RequestParameter {
    //Requested during LOGIN
    NICKNAME,
    NUM_OF_PLAYERS,
    GAME_MODE,
    TOWER_COLOR,
    WIZARD,
    //Requested during GAME (or some CHARACTERS)
    ASSISTANT,
    STUDENT_ENTRANCE,
    ISLAND_OR_TABLE,
    ISLAND,
    CLOUD,
    //Requested by some CHARACTERS
    STUDENT_DININGROOM,
    STUDENT_CARD,
    COLOR
}
