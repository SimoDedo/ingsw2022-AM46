package it.polimi.ingsw.Utils;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * New list used for players. Adds functionality useful to manage player and their teams
 */
public class PlayerList extends ArrayList<Player> { //FIXME: should throw exception on add if two have same nickname?

    /**
     * Gets the first player found who holds the towers. (TeamManager ensures only one for each team)
     * @param towerColor The color of the towers (also means the team)
     * @return The player who holds the towers
     */
    public Player getTowerHolder(TowerColor towerColor){
        for (Player player : this){
            if (player.getTowerColor().equals(towerColor))
                if(player.isTowerHolder())
                    return player;
        }
        return null;
    }

    /**
     * Returns the current number of players in team (not the one it should have, just how many there are)
     * @param towerColor The color of the tower (team) to count
     * @return The number of players
     */
    public int teamSize(TowerColor towerColor){
        return (int) this.stream().filter(player -> player.getTowerColor().equals(towerColor)).count();
    }

    /**
     * Returns the players of given team
     * @param towerColor The team of the player
     * @return a new PlayerList of players of a given team
     */
    public PlayerList getTeam(TowerColor towerColor){
        PlayerList playerListToReturn = new PlayerList();
        for(Player player : this){
            if(player.getTowerColor().equals(towerColor))
                playerListToReturn.add(player);
        }
        return playerListToReturn;
    }

    public Player get(String nickname) {
        return null;
    } //todo

}
