package it.polimi.ingsw.GameModel.BoardElements;
import it.polimi.ingsw.GameModel.Board.Player.*;

/**
 * Abstract class that extends BoardPiece with added owner (Player) parameter
 */
public abstract class BoardPieceWithOwner extends BoardPiece {

    /**
     * Holds player who owns the BoardPiece
     */
    Player owner;   //TODO: consider this implementation: to let access to BoardPieceWithOwner mutable it's "friendly"
                    //this also means that student and professor have access, while subclasses in other packages won't have it
                    //to avoid having more permissions than needed, we can opt to have BoardPieceWithOwnerMutable extend BoardPiece, this way it will hold the owner itself that can be private

    /**
     * Gives ID and owner
     * @param owner The owner
     */
    public BoardPieceWithOwner(Player owner) {
        super();
        this.owner = owner;
    }

    /**
     * Owner getter
     * @return Owner
     */
    public Player getOwner() {
        return owner;
    }
}
