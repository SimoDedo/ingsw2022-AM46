package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to model a piece of the board which contains students or towers
 * @param <T>
 */
public abstract class PawnContainer<T> extends BoardPieceWithOwnerMutable{
    /**
     * The list containing the pawns
     */
    private List<T> pawns;
    /**
     * Maximum pawn that it can hold
     */
    private int maxPawns;


    /**
     * Creates PawnContainer with owner and maxPawns
     * @param player
     * @param maxPawns
     */
    public PawnContainer(Player player, int maxPawns) {
        super(player);
        this.maxPawns = maxPawns;
    }

    /**
     * Places pawn in container
     * @param pawn
     */
    public void PlacePawn(T pawn){
        //TODO: should throw exception if object is already contained and if maxPawns is surpassed
        pawns.add(pawn);
    }

    /**
     * Removes and return pawn from container
     * @param pawnToRemove
     * @return
     */
    public T RemovePawn(T pawnToRemove){
        //TODO: should throw exception if empty? and if object is not in list
        int index = pawns.indexOf(pawnToRemove);
        return pawns.remove(index);
    }

    /**
     * Return number of pawns currently contained
     * @return
     */
    public int pawnCount(){
        return pawns.size();
    }

}
