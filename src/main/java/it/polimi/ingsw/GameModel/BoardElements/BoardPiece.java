package it.polimi.ingsw.GameModel.BoardElements;

import java.io.Serializable;

/**
 * Abstract class from which most game elements derive. Offers unique IDs.
 */

public abstract class BoardPiece implements Serializable {
    /**
     * Static attribute shared among all BoardPieces. Stores highest ID that hasn't been used yet
     */
    private static int maxID = 0;

    /**
     * ID of the concrete BoardPiece
     */
    public final int boardPieceID;

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
