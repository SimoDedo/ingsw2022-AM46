package it.polimi.ingsw.GameModel.BoardElements;
import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class that extends BoardPiece with added owner (Player) parameter
 */
public abstract class BoardPieceWithOwner extends BoardPiece {

    /**
     * Holds player who owns the BoardPiece
     */
    Player owner;

    /**
     * Gives ID and owner
     * @param owner the initial owner of the piece
     */
    public BoardPieceWithOwner(Player owner) {
        super();
        this.owner = owner;
    }

    /**
     * Getter for the owner of this piece.
     * @return the current owner of the piece
     */
    public Player getOwner() {
        return owner;
    }
}
