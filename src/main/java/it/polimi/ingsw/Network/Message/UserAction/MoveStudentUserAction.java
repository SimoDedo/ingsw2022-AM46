package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

/**
 * User action sent when a user wants to move a student to their dining room or to an island.
 */
public class MoveStudentUserAction extends UserAction{

    /**
     * The student chosen
     */
    private final int studentID;

    /**
     * The island or table chosen.
     */
    private final int islandOrTableID;

    public MoveStudentUserAction(String sender, int studentID, int islandOrTableID) {
        super(sender, UserActionType.MOVE_STUDENT);
        this.studentID = studentID;
        this.islandOrTableID = islandOrTableID;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getIslandOrTableID() {
        return islandOrTableID;
    }
}
