package it.polimi.ingsw.Network.Message.Info;

public class MoveStudentInfo extends Info{

    private int movedStudentID;

    private int destinationID;

    private int leftToMove;

    public MoveStudentInfo(int movedStudentID, int destinationID, int leftToMove) {
        super("Students successfully moved!");
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
