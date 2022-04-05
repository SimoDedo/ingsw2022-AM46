package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.Map;

/**
 * Class that creates the teams
 */
public class TeamManager {

    /**
     * Creates a PlayerList, creating player with given nicknames and teams (TowerColor)
     * @param gameConfig Configuration of the game
     * @param teamComposition Composition of teams. Contains nicknames and teams
     * @return A PlayerList containing all players in the Game
     * @throws IllegalArgumentException
     */
    public PlayerList create(GameConfig gameConfig, Map<String, TowerColor> teamComposition) throws IllegalArgumentException{
        PlayerList players = new PlayerList();
        int teamSize = gameConfig.getNumOfPlayers() / 2;

        for (Map.Entry<String, TowerColor> entry : teamComposition.entrySet()) {
            if(players.teamSize(entry.getValue()) >= teamSize)
                throw new IllegalArgumentException("Wrong team members distribution");
            boolean isTowerHolder = players.getTowerHolder(entry.getValue()) == null;
            players.add(new Player(entry.getKey(), entry.getValue(), isTowerHolder, gameConfig.getPlayerConfig()));
        }

        return players;
    }
}
