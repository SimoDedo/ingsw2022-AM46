package it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests resolve strategy for character 6
 */
class ResolveStrategyC6Test {

    /**
     * Tests that method correctly skips tower count
     * @throws GameOverException not tested
     */
    @Test
    void resolveIslandGroup() throws GameOverException {
        ResolveStrategyC6 resolveStrategyC6 = new ResolveStrategyC6();
        IslandGroup islandGroup = new IslandGroup(true);
        IslandTile islandTileToAdd = new IslandTile(null, false, null);
        islandTileToAdd.placePawn(new Student(Color.RED, null));
        islandTileToAdd.placePawn(new Student(Color.RED, null));
        islandTileToAdd.placePawn(new Student(Color.RED, null));
        islandTileToAdd.placePawn(new Student(Color.PINK, null));
        islandTileToAdd.placePawn(new Student(Color.PINK, null));
        List<IslandTile> islandTiles = new ArrayList<>();
        islandTiles.add(islandTileToAdd);
        islandGroup.addIslandTilesBefore(islandTiles);

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        PlayerList players = new PlayerList();
        players.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        players.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));

        islandGroup.conquer(players.getTowerHolder(TowerColor.BLACK));

        assertEquals(resolveStrategyC6.resolveIslandGroup(islandGroup, players, professorSet), players.getTowerHolder(TowerColor.WHITE),
                "unexpected player conquering island. perhaps towers are being mistakenly taken into account?");
    }
}