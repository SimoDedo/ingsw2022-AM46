package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.PlayerList;

/**
 * Standard strategy for the checkAndMoveProfessor method.
 */
public class CheckAndMoveProfessorStrategyStandard implements CheckAndMoveProfessorStrategy {

    public CheckAndMoveProfessorStrategyStandard() {}

    /**
     * Given a color in the professor set, this strategy gives the professor to the player that has
     * the most students of that color in their dining room. In the case of a draw no professor
     * is awarded.
     */
    public void checkAndMoveProfessor(Professor prof, PlayerList playerList, Color color) {
        Player winner = playerList.get(0);
        for (Player player : playerList) {
            if (player.getScore(color) > winner.getScore(color)) winner = player;
        }
        prof.setOwner(winner);
    }
}
