package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class that extends BoardPieceWithOwner with added setter method
 */
public abstract class BoardPieceWithOwnerMutable extends BoardPieceWithOwner{
    /**
     * Calls constructor of BoardPieceWithOwner
     * @param owner
     */
    public BoardPieceWithOwnerMutable(Player owner) {
        super(owner);
    }

    /**
     * Setter for the owner
     * @param owner
     */
    public void setOwner(Player owner){
        this.owner = owner;
    }
}
