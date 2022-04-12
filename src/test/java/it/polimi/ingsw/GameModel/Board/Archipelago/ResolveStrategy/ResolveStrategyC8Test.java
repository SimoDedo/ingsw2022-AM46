package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.TeamManager;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test resolve strategy for character 8
 */
class ResolveStrategyC8Test {

    /**
     * Tests that method correctly adds 2 influence to given player
     * @throws GameOverException not tested
     */
    @Test
    void resolveIslandGroup() throws GameOverException {
        ResolveStrategyC8 resolveStrategyC8 = new ResolveStrategyC8();
        IslandGroup islandGroup = new IslandGroup(true);
        IslandTile islandTileToAdd = new IslandTile(null, false, null);
        islandTileToAdd.placePawn(new Student(Color.RED, null));
        islandTileToAdd.placePawn(new Student(Color.RED, null));
        List<IslandTile> islandTiles = new ArrayList<>();
        islandTiles.add(islandTileToAdd);
        islandGroup.addIslandTilesBefore(islandTiles);

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        islandGroup.conquer(players.getTowerHolder(TowerColor.BLACK));

        resolveStrategyC8.setActivatingPlayer(players.getTowerHolder(TowerColor.BLACK));
        assertEquals(resolveStrategyC8.resolveIslandGroup(islandGroup, players, professorSet), players.getTowerHolder(TowerColor.BLACK));
    }
}