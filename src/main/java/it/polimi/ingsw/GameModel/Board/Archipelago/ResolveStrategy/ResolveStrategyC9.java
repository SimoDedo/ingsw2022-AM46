package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The strategy used when resolving an island if C9 has been activated in this turn.
 */
public class ResolveStrategyC9 implements ResolveStrategy{

    /**
     * The color that won't count towards the influence
     */
    private Color colorToIgnore = null;

    /**
     * Setter for the color to ignore during influence calculation
     * @param colorToIgnore The color to ignore
     */
    public void setColorToIgnore(Color colorToIgnore) {
        this.colorToIgnore = colorToIgnore;
    }


    /**
     * Method used to resolve an island when C9 is active. Doesn't count influence given by students
     * whose color is colorToIgnore
     * @param islandGroupToResolve The group to resolve
     * @param teams The teams inside the current game
     * @param professorSet Manager for the professor, used to know who owns each professor
     * @return The team which holds the most influence, or null if a tie happens
     */
    @Override
    public Team resolveIslandGroup(IslandGroup islandGroupToResolve, List<Team> teams, ProfessorSet professorSet) {
        HashMap<Team, Integer> scores = new HashMap<Team, Integer>();
        for(Team team : teams){ //Initializes the HashMap
            scores.put(team, 0);
        }
        for(Color color : Color.values()) { // Checks each color and gives to the right team the influence counted, based on the ownership of the professor
            if (!color.equals(colorToIgnore)) {
                Player professorOwner = professorSet.getProfessor(color).getOwner();
                for (Team team : teams) {
                    if (team.getMembers().contains(professorOwner)) {
                        int temp = scores.get(team);
                        scores.put(team, temp + islandGroupToResolve.countInfluence(color));
                    }
                }
            }
        }
        for(Team team : teams) { //Checks each team to see who should get the tower points
            if(team.getColor() == islandGroupToResolve.getTowerColor()){
                int temp = scores.get(team);
                scores.put(team, temp + islandGroupToResolve.getTowerCount());
            }
        }
        return getTeamWinner(scores);
    }

    /**
     * Returns the Team in the HashMap with the most influence
     * @param scores HashMap of the teams and their partial score
     * @return The team with the highest score, or null if more than one team holds the highest score
     */
    private Team getTeamWinner(Map<Team, Integer> scores){
        Team teamWinner = null;
        int max = 0;
        for(Team team : scores.keySet()){
            if(scores.get(team) > max){
                max = scores.get(team);
                teamWinner = team;
            }
            if(scores.get(team) == max)
                teamWinner = null;
        }
        return teamWinner;
    }
}
