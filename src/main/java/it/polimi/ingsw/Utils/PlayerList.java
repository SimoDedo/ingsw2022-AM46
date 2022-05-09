package it.polimi.ingsw.Utils;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * New list used for players. Adds functionality useful to manage player and their teams
 */
public class PlayerList extends ArrayList<Player> {

    /**
     * Override of the add method in ArrayList. Returns false if the player about to be added
     * has the same nickname as some other player already inside the list, otherwise it adds the
     * player into the list normally. Neutral players are excluded from this rule.
     *
     * @param newPlayer the player to add
     * @return true if the insertion is successful, false if the new player has the same nickname
     * as another player already inside the list
     */
    @Override
    public boolean add(Player newPlayer) {
        for (Player player : this) {
            if (player.getNickname().equals(newPlayer.getNickname())
                    && !newPlayer.getNickname().equals("NEUTRAL")) return false;
        }
        return super.add(newPlayer);
    }


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

    public List<Player> getTowerHolders(){
        return this.stream().filter(Player::isTowerHolder).toList();

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

    /**
     * Finds a player in the list who has given nickname
     * @param nickname The nickname of the player to find
     * @return The player found, or null if no player has given nickname
     */
    public Player getByNickname(String nickname) {
        return stream().filter(player -> player.getNickname().equals(nickname)).findAny().orElse(null);
    }


}
