package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class that extends BoardPieceWithOwner with an added setter method
 */
public abstract class BoardPieceWithOwnerMutable extends BoardPieceWithOwner{
    /**
     * Calls constructor of BoardPieceWithOwner
     * @param owner the initial owner of the piece
     */
    public BoardPieceWithOwnerMutable(Player owner) {
        super(owner);
    }

    /**
     * Setter for the owner of this piece.
     * @param owner the future owner of the piece
     */
    public void setOwner(Player owner){
        this.owner = owner;
    }
}
