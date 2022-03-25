package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.TowerColor;

/**
 * Models the Student piece as a BoardPiece. Each tower has an owner and a color
 */
public class Tower extends BoardPieceWithOwner{
    /**
     * Color of the tower. Can't change, thus it's final
     */
    private final TowerColor towerColor;

    /**
     * Creates Tower with Color and gives ID and owner
     *
     * @param owner the initial owner of this tower
     */
    public Tower(TowerColor towerColor,Player owner) {
        super(owner);
        this.towerColor = towerColor;
    }

    /**
     * Getter for the TowerColor
     * @return the current owner of this tower
     */
    public TowerColor getTowerColor() {
        return towerColor;
    }
}
