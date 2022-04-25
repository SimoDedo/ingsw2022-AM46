package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.*;

import java.util.*;

/**
 * Abstract class to model a section of the board which contains students or towers
 * @param <T> the type of the pawns contained, extends BoardPiece
 */
public abstract class PawnContainer<T extends BoardPiece> extends BoardPieceWithOwnerMutable {
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
    public void placePawns(List<T> pawns) throws IllegalArgumentException {
        for (T pawn : pawns) {
            placePawn(pawn);
        }

    }

    /**
     * Removes and return pawn from container
     * @param pawnToRemove pawn to be removed
     * @return true if the pawn was removed successfully, false otherwise
     */
    public boolean removePawn(T pawnToRemove) {
        return pawns.remove(pawnToRemove);
    }

    /**
     *  Alternative method to remove a pawn from a container referencing it by its ID
     *
     * @param ID the ID of the pawn to remove
     * @return the freshly removed pawn
     */
    public T removePawnByID(int ID) {
        T pawn = getPawnByID(ID);
        removePawn(pawn);
        return pawn;
    }

    /**
     * Removes the first pawn in the list if present
     * @return the removed pawn, null if no pawn was contained
     */
    public T removePawn() {
        if (pawns.size() > 0)
            return pawns.remove(0);
        else
            return null;
    }

    /**
     * @return the number of pawns currently contained
     */
    public int pawnCount() {
        return pawns.size();
    }

    /**
    * Getter for the max number of pawns this container can hold
    */
    public int getMaxPawns() { return maxPawns; }

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
    public List<Integer> getPawnIDs() {
        List<Integer> IDs = new ArrayList<>();
        for(T pawn : pawns){
            IDs.add(pawn.getID());
        }
        return IDs;
    }

    public T getPawnByID(int ID) {
        return pawns.stream().filter(pawn -> pawn.getID() == ID).findAny().orElse(null);
    }

}
