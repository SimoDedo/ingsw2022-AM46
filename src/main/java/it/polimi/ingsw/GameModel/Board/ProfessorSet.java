package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves as a container of Professors that has certain useful methods to access them in
 * bulk or modify them.
 */
public class ProfessorSet {

    private final Map<Color, Professor> professors = new HashMap<>();

    /**
     * Constructor for ProfessorSet. Creates an initially owner-less Professor for each Color.
     */
    public ProfessorSet() {
        for (Color color : Color.values()) {
            professors.put(color, new Professor(null, color));
        }
    }

    /**
     * Getter for the Professor of the given Color.
     *
     * @param color the color of the professor to find
     * @return the professor of that color
     */
    public Professor getProfessor(Color color) {
        return professors.get(color);
    }

    /**
     * Setter for the owner of the Professor of the given Color.
     *
     * @param color the color of the professor to access
     * @param owner the future owner of the professor
     */
    public void setOwner(Color color, Player owner) {
        getProfessor(color).setOwner(owner);
    }

    /**
     * Method that calculates and returns the number of professors currently owned by a given Team.
     * FIXME: change Team to TowerColor
     */
    public int getNumberOfProfessors(Team team) {
        int score = 0;
        List<Player> teamMembers = new ArrayList<>(team.getMembers());
        for (Professor prof : professors.values()) {
            if (teamMembers.contains(prof.getOwner())) score++;
        }
        return score;
    }
}
