package it.polimi.ingsw.GameModel.Board.Player;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.BoardElements.TowerContainer;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

public class TowerSpace extends TowerContainer {

    private final TowerColor color;

    /**
     * Creates PawnContainer with owner and maxPawns
     *
     * @param player   the owner of the container
     * @param maxPawns the maximum number of pawns the container can hold
     */
    public TowerSpace(Player player, int maxPawns, TowerColor color) {
        super(player, maxPawns);
        this.color = color;
        fillInitial(maxPawns);
    }

    /**
     * Fills TowerSpace with towers when it is initialized
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
    public Tower takeTower(){
        if (getTowersPlaced() == getMaxPawns()) {
            return null;
        }
        return removePawn();

    }

    public void placeTower(Tower tower){
        placePawn(tower);
    }

    /**
     * @return amount of towers removed from the tower space since the start of the game
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
