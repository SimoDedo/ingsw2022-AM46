package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.HashMap;
import java.util.Map;

/**
 * The standard strategy used when resolving an island
 */
public class ResolveStrategyStandard implements ResolveStrategy{

    /**
     * Method used to resolve an island
     * @param islandGroupToResolve The island to resolve
     * @param players The players of the current game
     * @param professorSet Manager for the professor, used to know who owns them
     * @return The player holding the towers of the team which holds the most influence, or null if a tie happens
     */
    @Override
    public Player resolveIslandGroup(IslandGroup islandGroupToResolve, PlayerList players, ProfessorSet professorSet) {
        HashMap<TowerColor, Integer> scores = new HashMap<>();
        for(TowerColor towerColor : TowerColor.values()){ //Initializes the HashMap
            scores.put(towerColor, 0);
        }

        for(Color color : Color.values()){ // Checks each color and gives to the right team the influence counted, based on the ownership of the professor
            Player professorOwner = professorSet.getProfessor(color).getOwner();
            if(professorOwner != null){
                int temp = scores.get(professorOwner.getTowerColor());
                scores.put(professorOwner.getTowerColor(), temp + islandGroupToResolve.countInfluence(color));
            }
        }

        for(TowerColor towerColor : TowerColor.values()) { //Checks each team to see who should get the tower points
            if(towerColor == islandGroupToResolve.getTowerColor()){
                int temp = scores.get(towerColor);
                scores.put(towerColor, temp + islandGroupToResolve.getTowerCount());
            }
        }
        return players.getTowerHolder(getTeamWinner(scores));
    }

    /**
     * Returns the Team in the HashMap with the most influence
     * @param scores HashMap of towerColors(teams) and their score
     * @return The towerColor(team) with the highest score, or null if more than one team holds the highest score
     */
    private TowerColor getTeamWinner(Map<TowerColor, Integer> scores){
        TowerColor teamWinner = null;
        int max = 0;
        for(TowerColor towerColor : TowerColor.values()){
            if(scores.get(towerColor) > max){
                max = scores.get(towerColor);
                teamWinner = towerColor;
            }
            else if(scores.get(towerColor) == max)
                teamWinner = null;
        }
        return teamWinner;
    }
}
