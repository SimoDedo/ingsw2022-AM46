package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class which extends PawnContainer, using Towers specifically
 */
public abstract class TowerContainer extends PawnContainer<Tower> {
    /**
     * Creates TowerContainer with owner and maxPawns
     *
     * @param player the initial owner of the container
     * @param maxPawns the max number of towers the container can hold
     */
    public TowerContainer(Player player, int maxPawns) {
        super(player, maxPawns);
    }

}
