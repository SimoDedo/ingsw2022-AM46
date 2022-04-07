package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The strategy used when resolving an island if C3 has been activated in this turn.
 */
public class ResolveStrategyC3 implements ResolveStrategy{

    /**
     * The island selected by the user to be resolved
     */
    private IslandTile islandTileSelected;

    /**
     * Setter for the selected island tile (=> group) to be resolved.
     * @param islandTileSelected
     */
    public void setIslandTileSelected(IslandTile islandTileSelected) {
        this.islandTileSelected = islandTileSelected;
    }

    /**
     * Method to be used to resolve the island. It uses already stored information to know which
     * IslandGroup to resolve.
     * @param teams The teams inside the current game
     * @param professorSet Manager for the professor, used to know who owns each professor
     * @return The team which holds the most influence, or null if a tie happens
     */
    public Team resolveIslandGroup(List<Team> teams, ProfessorSet professorSet){
        return this.resolveIslandGroup(islandTileSelected.getIslandGroup(), teams, professorSet);
    }
    /**
     * Method used to resolve an island group. It doesn't change from the standard strategy,
     * and will be called by the Archipelago once when C3 is activated, and to resolve the
     * island MotherNature lands on normally.
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
        for(Color color : Color.values()){ // Checks each color and gives to the right team the influence counted, based on the ownership of the professor
            Player professorOwner = professorSet.getProfessor(color).getOwner();
            for(Team team : teams){
                if(team.getMembers().contains(professorOwner)){
                    int temp = scores.get(team);
                    scores.put(team, temp + islandGroupToResolve.countInfluence(color));
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
