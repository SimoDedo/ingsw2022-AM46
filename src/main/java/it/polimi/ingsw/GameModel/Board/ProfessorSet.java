package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategy;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the Professors and offers useful methods to retrieve the right professor or
 * change their ownership.
 */
public class ProfessorSet {

    private final Map<Color, Professor> professors = new HashMap<>();

    /**
     * Strategy for determining how players conquer professors or snatch them from other players.
     */
    private CheckAndMoveProfessorStrategy checkAndMoveProfessorStrategy = new CheckAndMoveProfessorStrategyStandard();

    public ProfessorSet() {
        for (Color color : Color.values()) {
            professors.put(color, new Professor(null, color));
        }
    }

    /**
     * Getter for the Professor of the given color.
     * @param color the color of the professor to retrieve
     * @return the professor of that color
     */
    public Professor getProfessor(Color color) {
        return professors.get(color);
    }

    /**
     * Setter for the owner of a given professor.
     * @param color the color of the professor to access
     * @param owner the future owner of that professor
     */
    public void setOwner(Color color, Player owner) {
        getProfessor(color).setOwner(owner);
    }

    /**
     * Method for calculating the number of professor that a given team possesses.
     * @param towerColor the tower color of the given team
     * @return the number of professors owned by the members of the team
     */
    public int getNumberOfProfessors(TowerColor towerColor) {
        int score = 0;
        for (Professor prof : professors.values()) {
            if (prof.getOwner().getTowerColor().equals(towerColor)) score++;
        }
        return score;
    }

    public Player determineStrongestPlayer(Player player1, Player player2) {
        if (getNumberOfProfessors(player1.getTowerColor()) > getNumberOfProfessors(player2.getTowerColor()))
            return player1;
        else return player2;
    }

    public void checkAndMoveProfessor(Color color) {
        checkAndMoveProfessorStrategy.checkAndMoveProfessor(color);
        //todo: actual implementation of the strategy. also add needed parameters to the signature
        // calls professorset.setowner when winner is found
    }

    /**
     * Setter for the checkAndMoveProfessor strategy.
     * @param checkAndMoveProfessorStrategy the strategy to set as current
     */
    public void setCheckAndMoveProfessorStrategy(CheckAndMoveProfessorStrategy checkAndMoveProfessorStrategy) {
        this.checkAndMoveProfessorStrategy = checkAndMoveProfessorStrategy;
    }

}
