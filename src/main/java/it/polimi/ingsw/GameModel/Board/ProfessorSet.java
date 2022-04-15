package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategy;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains the Professors and offers useful methods to retrieve the right professor or
 * change their ownership.
 */
public class ProfessorSet {

    private final List<Professor> professors = new ArrayList<>();
    //note: an arraylist is slightly overkill, but it's better to keep it this way and eventually downgrade it to a set after the view is done

    /**
     * Strategy for determining how players conquer professors or snatch them from other players.
     */
    private CheckAndMoveProfessorStrategy checkAndMoveProfessorStrategy = new CheckAndMoveProfessorStrategyStandard();

    public ProfessorSet() {
        for (Color color : Color.values()) {
            professors.add(new Professor(null, color));
        }
    }

    /**
     * Getter for the Professor of the given color.
     * @param color the color of the professor to retrieve
     * @return the professor of that color
     */
    public Professor getProfessor(Color color) {
        return professors
                .stream()
                .filter(prof -> prof.getColor() == color)
                .findAny()
                .orElse(null);
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
        for (Professor prof : professors) {
            if (prof.getOwner().getTowerColor().equals(towerColor)) score++;
        }
        return score;
    }

    public Player determineStrongestPlayer(Player player1, Player player2) {
        if (getNumberOfProfessors(player1.getTowerColor()) > getNumberOfProfessors(player2.getTowerColor()))
            return player1;
        else return player2;
    }

    public void checkAndMoveProfessor(PlayerList playerList, Color color) {
        checkAndMoveProfessorStrategy.checkAndMoveProfessor(getProfessor(color), playerList, color);
    }

    /**
     * Setter for the checkAndMoveProfessor strategy.
     * @param checkAndMoveProfessorStrategy the strategy to set as current
     */
    public void setCheckAndMoveProfessorStrategy(CheckAndMoveProfessorStrategy checkAndMoveProfessorStrategy) {
        this.checkAndMoveProfessorStrategy = checkAndMoveProfessorStrategy;
    }

    //region State Observer methods

    /**
     * Method to observe which Professor is owned by who
     * @return An HashMap with the color of the professor as Key and its owner as Object (null if no player owns it)
     */
    public HashMap<Color, String> getProfessorsOwner(){
        HashMap<Color, String> result = new HashMap<>();
        for (Professor professor : professors){
            String owner = professor.getOwner() == null ? null : professor.getOwner().getNickname();
            result.put(professor.getColor(), owner);
        }
        return result;
    }

    //endregion

}
