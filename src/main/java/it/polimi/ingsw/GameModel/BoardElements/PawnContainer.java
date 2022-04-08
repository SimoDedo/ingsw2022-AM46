package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private int maxPawns;


    /**
     * Creates PawnContainer with owner and maxPawns
     * @param player the owner of the container
     * @param maxPawns the maximum number of pawns the container can hold
     */
    public PawnContainer(Player player, int maxPawns) {
        super(player);
        this.pawns = new ArrayList<>();
        this.maxPawns = maxPawns;
    }

    /**
     * Places pawn in container. Throws exception if the container is already full, or if the pawn
     * is already inside the container.
     * @param pawn the pawn to be placed
     */
    public void placePawn(T pawn) throws IllegalArgumentException {
        if (maxPawns >=0 && pawnCount() == maxPawns) {
            throw new IllegalArgumentException("The container is already full");
        }
        if (pawns.contains(pawn)) {
            throw new IllegalArgumentException("The pawn is already inside this container");
        }
        pawns.add(pawn);
    }

    /**
     * Wrapper that calls placePawn() over all the pawns in the given list.
     * @param pawns list of pawns to be placed inside this container
     */
    public void placePawns(List<T> pawns) {
        for (T pawn : pawns) {
            placePawn(pawn);
        }

    }

    /**
     * Removes and return pawn from container
     * @param pawnToRemove pawn to be removed
     * @return the pawn, removed from the container
     */
    public T removePawn(T pawnToRemove) {
        int index = pawns.indexOf(pawnToRemove);
        return removePawnByIndex(index);
    }

    /**
     * Alternative version of removePawn using list index
     * @param index index of the pawn to be removed
     * @return the pawn, removed from the container
     * @throws IllegalArgumentException When no pawn with such ID is contained
     */
    public T removePawnByIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= getMaxPawns()) {
            throw new IndexOutOfBoundsException("Index not inside this container's range");
        }
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
     * Getter for the list, given through a copy; grants subclasses access to the list to perform
     * operations through a copy. Modification is restricted to the public PlaceAndRemove operations
     * @return A new list, containing references to the pawns inside the container
     */
    protected List<T> getPawns() {
        return new ArrayList<>(pawns);
    }

    /**
     * Getter for a list of IDs of pawns contained in the PawnContainer
     * @return A new list of Integers containing the IDs
     */
    public  List<Integer> getPawnsIDs(){
        List<Integer> IDs = new ArrayList<>();
        for(T pawn : pawns){
            IDs.add(pawn.getID());
        }
        return IDs;
    }

    public T getPawnByID(int ID) throws NoSuchElementException {
        Predicate<T> test = pawn -> pawn.getID() == ID;
        return pawns.stream().filter(pawn -> pawn.getID() == ID).findAny().orElseThrow(() -> new NoSuchElementException("No such element in container"));
    }

    public T removePawnByID(int ID) { //todo
        return null;
    }

}
