package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.PlayerList;

/**
 * Modified CheckAndMoveProfessor strategy set up by Character 2. This strategy is different
 * from the standard one in that it stores a preferred Player, who wins the professor in case of
 * draw.
 */
public class CheckAndMoveProfessorStrategyC2 implements CheckAndMoveProfessorStrategy {

    /**
     * Player who activated and paid for C2. This player will win the professor in the case of a
     * draw.
     */
    private Player activator;

    public String getActivatingPlayer(){
        return activator == null ? null : activator.getNickname();
    }

    /**
     * Constructor for the strategy. Sets the activator attribute to the player who played C2 in
     * this turn.
     * @param activator the activator of C2
     */
    public CheckAndMoveProfessorStrategyC2(Player activator) {
        this.activator = activator;
    }

    /**
     * Given a color in the professor set, this strategy gives the professor to the player that has
     * the most students of that color in their dining room. In the case of a draw, if one of the two
     * drawing player is the activator of the character, they win the professor.
     */
    public void checkAndMoveProfessor(Professor prof, PlayerList playerList, Color color) {
        Player winner = playerList.get(0);
        for (Player player : playerList) {
            if (player.getScore(color) > winner.getScore(color)) winner = player;
            else if (player.equals(activator) && player.getScore(color) == winner.getScore(color)) winner = player;
        }
        prof.setOwner(winner);
    }
}
