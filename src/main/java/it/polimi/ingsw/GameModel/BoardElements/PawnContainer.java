package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract class to model a section of the board which contains students or towers
 * @param <T> the type of the pawns contained, extends BoardPiece
 */
public abstract class PawnContainer<T extends BoardPiece> extends BoardPieceWithOwnerMutable{
    /**
     * The list containing the pawns
     */
    private List<T> pawns;

    /**
     * Maximum number of pawns that the container can hold. If set at -1, no limit is set
     */
    private int maxPawns; //if -1 -> no limit


    /**
     * Creates PawnContainer with owner and maxPawns
     * @param player the owner of the container
     * @param maxPawns the maximum number of pawns the container can hold
     */
    public PawnContainer(Player player, int maxPawns) {
        super(player);
        this.pawns = new ArrayList<T>();
        this.maxPawns = maxPawns;
    }

    /**
     * Places pawn in container
     * @param pawn the pawn to be placed
     */
    public void placePawn(T pawn){
        if(maxPawns>=0 && this.pawnCount() == maxPawns){
            //TODO: should throw exception if object is already contained and if maxPawns is surpassed
        }
        pawns.add(pawn);
    }

    /**
     * Removes and return pawn from container
     * @param pawnToRemove pawn to be removed
     * @return the pawn, removed from the container
     */
    public T removePawn(T pawnToRemove){
        //TODO: should throw exception if object is not in list
        int index = pawns.indexOf(pawnToRemove);
        return pawns.remove(index);
    }
    /**
     * Alternative version of removePawn using list index
     * @param index index of the pawn to be removed
     * @return the pawn, removed from the container
     */
    public T removePawnByIndex(int index) {
        return pawns.remove(index);
    }

    /**
     * @return the number of pawns currently contained
     */
    public int pawnCount(){
        return pawns.size();
    }

    /**
    * Getter for the max number of pawns this container can hold
    */
    public int getMaxPawns() {return maxPawns; }

    /**
     * Getter for the list, given through a copy; grants subclasses access to the list to perform operations through a copy. Modification is restricted to the public Place and Remove operations
     * @return
     */
    protected List<T> getPawns() {
        List<T> copy = new ArrayList<>();
        copy.addAll(pawns);
        return copy;
    }

    public T getPawnByID(int ID){
        Predicate<T> test = pawn -> pawn.getID() == ID;
        return pawns.stream().filter(pawn -> pawn.getID() == ID).findAny().orElse(null);
    }

}
