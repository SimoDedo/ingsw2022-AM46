package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.GameModel.Board.Player.Player;

/**
 * Class which models the five Professors of the game. Extends BoardPieceWithOwnerMutable
 * to model change in player ownership.
 */
public class Professor extends BoardPieceWithOwnerMutable{
    /**
     * Color of the professor. Can't change, thus it's final
     */
    private final Color color;

    /**
     * Creates a Professor with owner and color
     * @param color the professor's color
     * @param owner the initial owner of the professor
     */
    public Professor(Player owner, Color color){
        super(owner);
        this.color = color;
    }

    /**
     * Getter for the color
     * @return the professor's color
     */
    public Color getColor() {
        return color;
    }
}
