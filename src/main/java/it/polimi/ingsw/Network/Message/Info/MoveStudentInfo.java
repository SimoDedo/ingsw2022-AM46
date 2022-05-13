package it.polimi.ingsw.Network.Message.Info;

public class MoveStudentInfo extends Info{

    private final int movedStudentID;

    private final int destinationID;

    private final int leftToMove;

    public MoveStudentInfo(int movedStudentID, int destinationID, int leftToMove) {
        super("Student successfully moved!");
        this.movedStudentID = movedStudentID;
        this.destinationID = destinationID;
        this.leftToMove = leftToMove;
    }

    public int getMovedStudentID() {
        return movedStudentID;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public int getLeftToMove() {
        return leftToMove;
    }
}
