package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.PawnContainer;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

public class TowerSpace extends PawnContainer<Tower> {

    private final TowerColor color;

    /**
     * Creates PawnContainer with owner and maxPawns
     *
     * @param player   the owner of the container
     * @param maxPawns the maximum number of pawns the container can hold
     */
    public TowerSpace(Player player, int maxPawns, TowerColor color) {
        super(player, maxPawns);
        fillInitial(maxPawns);
        this.color = color;
    }

    /**
     * Fills TowerSpace when it is initialized
     *
     * @param maxPawns the maximum number of pawns the container can hold
     */
    private void fillInitial(int maxPawns) {
        for(int i = 0; i < maxPawns; i ++) {
            placePawn(new Tower(getColor(),getOwner()));
        }
    }

    /**
     * @return Tower at index 0, removed from the container
     */
    public Tower takeTower() throws GameOverException {
        if (getTowersPlaced() == getMaxPawns()) {
            System.out.print("win :)");
        }
        return removePawnByIndex(0);

    }

    public void placeTower(Tower tower){
        placePawn(tower);
    }

    /**
     * @return pawns placed
     */
    public int getTowersPlaced(){
        return getMaxPawns() - pawnCount();

    }

    /**
     * @return tower color
     */
    public TowerColor getColor() {
        return color;
    }




}
