package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the IslandGroup class
 */
class IslandGroupTest {

    /**
     * Tests that starting IslandGroup has mother nature as it should
     */
    @Test
    void hasMotherNature() {
        IslandGroup islandGroup = new IslandGroup(true);
        assertTrue(islandGroup.hasMotherNature());
    }

    /**
     * Tests that methods returns an IslandTile containing MotherNature
     */
    @Test
    void getMotherNatureTile() {
        IslandGroup islandGroup = new IslandGroup(true);
        assertTrue(islandGroup.getMotherNatureTile().hasMotherNature());
    }

    @Test
    void resolveWinner() {
    }

    @Test
    void conquer() {
    }

    /**
     * Ensures IslandGroup has no IslandTiles after operation
     */
    @Test
    void removeIslandTiles() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = islandGroup.removeIslandTiles();
        assertTrue(islandTiles.get(0) != null && islandTiles.get(0).hasMotherNature());
        assertFalse(islandGroup.hasIslandTile(islandTiles.get(0)));
    }

    @Test
    void selfDestruct() {
    }

    /**
     * Tests if method correctly puts IslandTiles after those already present
     */
    @Test
    void addIslandTilesBefore() {
        IslandGroup islandGroup1 = new IslandGroup(true);
        IslandGroup islandGroup2 = new IslandGroup(false);
        IslandGroup islandGroup3 = new IslandGroup(false);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandGroup2.addIslandTilesBefore(islandGroup1.removeIslandTiles());
        islandGroup3.addIslandTilesBefore(islandGroup2.removeIslandTiles());
        islandTiles = islandGroup3.removeIslandTiles();
        assertTrue(islandTiles.get(0).hasMotherNature() && islandTiles.size() == 3);
    }

    /**
     * Tests if method correctly puts IslandTiles after those already present
     */
    @Test
    void addIslandTilesAfter() {
        IslandGroup islandGroup1 = new IslandGroup(true);
        IslandGroup islandGroup2 = new IslandGroup(false);
        IslandGroup islandGroup3 = new IslandGroup(false);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandGroup2.addIslandTilesAfter(islandGroup1.removeIslandTiles());
        islandGroup3.addIslandTilesAfter(islandGroup2.removeIslandTiles());
        islandTiles = islandGroup3.removeIslandTiles();
        assertTrue(islandTiles.get(2).hasMotherNature() && islandTiles.size() == 3);
    }

    /**
     * Adds IslandTile to an IslandGroup and tests if methods returns true
     */
    @Test
    void hasIslandTile() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandTiles.add(new IslandTile(null, -1, false,null ));
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(islandGroup.hasIslandTile(islandTiles.get(0)));
    }

    @Test
    void placeStudent() { //Test not needed, calls a single function that is tested and works

    }

    /**
     *
     */
    @Test
    void getTowerColor() {//TODO: modify and add javadoc description once conquer is done. Right now just inserts a tower of TowerColor at start and checks, will have to use conquer (as would a real game)
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandTiles.add(new IslandTile(null, -1, false,null ));
        islandTiles.get(0).swapTower(new Tower(TowerColor.BLACK, null));
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(islandGroup.getTowerColor() == TowerColor.BLACK);
    }
}