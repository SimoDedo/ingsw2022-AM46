package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.Utils.Enum.Color;

/**
 * Standard strategy for the checkAndMoveProfessor method.
 * famf
 */
public class CheckAndMoveProfessorStrategyStandard implements CheckAndMoveProfessorStrategy {

    public CheckAndMoveProfessorStrategyStandard() {}

    /**
     * Given a color in the professor set, this strategy gives the professor to the player that has
     * the most students of that color in their dining room. In the case of a draw no professor
     * is awarded.
     * @param color
     */
    public void checkAndMoveProfessor(Color color) {
        //todo: implementation
    }
}
