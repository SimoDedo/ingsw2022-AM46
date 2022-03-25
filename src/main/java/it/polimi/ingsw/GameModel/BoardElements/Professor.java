package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.GameModel.Board.Player.*;

/**
 * Class which models the professor, extends BoardPieceWithOwnerMutable to model change in player ownership
 */
public class Professor extends BoardPieceWithOwnerMutable{
    /**
     * Color of the professor. Can't change, thus it's final
     */
    private final Color color;

    /**
     * Creates a Professor with owner and color
     * @param player
     * @param color
     */
    public Professor(Player player,Color color){
        super(player);
        this.color = color;
    }

    /**
     * Getter for the color
     * @return The professor color
     */
    public Color getColor() {
        return color;
    }
}
