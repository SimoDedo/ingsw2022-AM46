package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deprecated
 */
public class TeamsFactory {

    public static List<Team> create(int game_players){
        // pick  first random TowerColor
        int colorIndex = new Random().nextInt(Color.values().length);
        List<Team> teams = new ArrayList<>();
        switch(game_players){
            case 2: case 3:
                for(int team_n = 0; team_n < game_players; team_n ++, colorIndex ++){
                    teams.add(new Team(TowerColor.values()[colorIndex % Color.values().length], game_players));

                }
            case 4:
                for(int team_n = 0; team_n < 2; team_n ++){
                    teams.add(new Team(TowerColor.values()[colorIndex % Color.values().length], game_players));
                }
        }
        return teams;

    }
}
