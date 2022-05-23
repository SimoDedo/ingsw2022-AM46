package it.polimi.ingsw.Utils.Enum;

/**
 * Represents the possible user action that the user can take.
 * Each also has two description used to show info to the user.
 */
public enum UserActionType {
    LOGIN,
    GAME_SETTINGS("has chosen the game settings", "is choosing the game settings"),
    TOWER_COLOR("has chosen a tower color", "is choosing a tower color"),
    WIZARD("has chosen a wizard", "is choosing a wizard"),
    WAIT_GAME_START(null, "is waiting for the game to start"),
    PLAY_ASSISTANT("played an assistant", "is playing an assistant"),
    MOVE_STUDENT("has moved a student", "is moving a student"),
    MOVE_MOTHER_NATURE("has moved mother nature", "is moving mother nature"),
    TAKE_FROM_CLOUD("has chosen a cloud", "is choosing a cloud"),
    USE_CHARACTER("has used a character", null),
    USE_ABILITY("has activated a character ability", "can activate a character ability"),
    END_GAME,
    END_TURN;

    private String actionTakenDesc;
    private String actionToTakeDesc;

    UserActionType(){}

    UserActionType(String actionTakenDesc, String actionToTakeDesc) {
        this.actionTakenDesc = actionTakenDesc;
        this.actionToTakeDesc = actionToTakeDesc;
    }

    public String getActionTakenDesc() {
        return actionTakenDesc;
    }

    public String getActionToTake() {
        return actionToTakeDesc;
    }
}
