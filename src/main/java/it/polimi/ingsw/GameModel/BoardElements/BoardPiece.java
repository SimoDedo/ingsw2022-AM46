package it.polimi.ingsw.GameModel.BoardElements;

/**
 * Abstract class from which most game elements derives. Offers IDs.
 */

public abstract class BoardPiece {
    /**
     * Static attribute shared among all BoardPieces. Stores highest ID that has not been used yet
     */
    private static int maxID;

    /**
     * ID of the concrete BoardPiece
     */
    private int boardPieceID;

    /**
     * Creates BoardPiece assigning an ID
     */
    public BoardPiece(){
        boardPieceID = getMaxID();
    }

    /**
     * Increments maxID
     * @return The maxID before it is incremented
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
