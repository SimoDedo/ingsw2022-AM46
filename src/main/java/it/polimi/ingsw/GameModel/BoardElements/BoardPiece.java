package it.polimi.ingsw.GameModel.BoardElements;

/**
 * Abstract class from which most game elements derive. Offers unique IDs.
 */

public abstract class BoardPiece {
    /**
     * Static attribute shared among all BoardPieces. Stores highest ID that hasn't been used yet
     */
    private static int maxID = 0;

    /**
     * ID of the concrete BoardPiece
     */
    public int boardPieceID;

    /**
     * Creates BoardPiece, assigning it an ID
     */
    public BoardPiece(){
        boardPieceID = getMaxID();
    }

    /**
     * Increments maxID
     * @return the maximum ID, incrementing it afterwards
     */
    private static int getMaxID(){
        int temp = maxID;
        maxID++;
        return temp;
    }

    /**
     * @return The ID of the BoardPiece
     */
    public int getID(){
        return boardPieceID;
    }

}
