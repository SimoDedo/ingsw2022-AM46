package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfessorSet {

    private final HashMap<Color, Professor> professors = new HashMap<>();

    public ProfessorSet() {
        for (Color color : Color.values()) {
            professors.put(color, new Professor(null, color));
        }
    }

    public Professor getProfessor(Color color) {
        return professors.get(color);
    }

    public void setOwner(Color color, Player owner) {
        getProfessor(color).setOwner(owner);
    }

    public int getNumberOfProfessors(Team team) {
        int score = 0;
        List<Player> teamMembers = new ArrayList<>(team.getMembers());
        for (Professor prof : professors.values()) {
            if (teamMembers.contains(prof.getOwner())) score++;
        }
        return score;

    }
}
