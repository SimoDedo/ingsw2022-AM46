package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;

public class MoveStudentUserAction extends UserAction{

    private final int studentID;

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
