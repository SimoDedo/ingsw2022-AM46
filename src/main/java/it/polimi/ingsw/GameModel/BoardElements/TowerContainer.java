package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Abstract class which extends PawnContainer using Towers specifically
 */
public abstract class TowerContainer extends PawnContainer<Tower>{
    /**
     * Creates TowerContainer with owner and maxPawns
     *
     * @param player
     * @param maxPawns
     */
    public TowerContainer(Player player, int maxPawns) {
        super(player, maxPawns);
    }

}
