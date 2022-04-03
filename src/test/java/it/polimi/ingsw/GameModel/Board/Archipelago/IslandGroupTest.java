package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.hamcrest.core.Is;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;




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

    /**
     * Test that counting influence works when IslandGroup contains multiple IslandTiles
     */
    @Test
    void countInfluence() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandTiles.add(new IslandTile(null, false, null));
        islandTiles.add(new IslandTile(null, false, null));
        islandTiles.get(0).placePawn(new Student(Color.PINK, null));
        islandTiles.get(0).placePawn(new Student(Color.PINK, null));
        islandTiles.get(0).placePawn(new Student(Color.PINK, null));
        islandTiles.get(0).placePawn(new Student(Color.PINK, null));
        islandTiles.get(0).placePawn(new Student(Color.RED, null));
        islandTiles.get(0).placePawn(new Student(Color.RED, null));
        islandTiles.get(0).placePawn(new Student(Color.RED, null));
        islandTiles.get(1).placePawn(new Student(Color.PINK, null));
        islandTiles.get(1).placePawn(new Student(Color.PINK, null));
        islandTiles.get(1).placePawn(new Student(Color.PINK, null));
        islandGroup.addIslandTilesAfter(islandTiles);
        assertTrue(islandGroup.countInfluence(Color.PINK) == 7 && islandGroup.countInfluence(Color.RED) == 3 && islandGroup.countInfluence(Color.BLUE) == 0);
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
        islandTiles.add(new IslandTile(null, false,null ));
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
        islandTiles.add(new IslandTile(null, false,null ));
        islandTiles.get(0).swapTower(new Tower(TowerColor.BLACK, null));
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(islandGroup.getTowerColor() == TowerColor.BLACK);
    }

    @Test
    void findStudentByID(){
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        IslandTile islandTile = new IslandTile(null, true, null);
        islandTile.moveStudent(new Student(Color.GREEN, null));
        islandTile.moveStudent(new Student(Color.PINK, null));
        islandTile.moveStudent(new Student(Color.RED, null));
        islandTile.moveStudent(new Student(Color.BLUE, null));
        Student studentToFind = new Student(Color.YELLOW, null);
        islandTile.moveStudent(studentToFind);
        islandTiles.add(islandTile);
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(studentToFind.equals(islandGroup.findStudentByID(studentToFind.getID())));
    }

    @Test
    void findIslandTileByID(){
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        IslandTile islandTileToFind = new IslandTile(null, true, null);
        IslandTile islandTile1 = new IslandTile(null, true, null);
        islandTiles.add(islandTileToFind);
        islandTiles.add(islandTile1);
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(islandTileToFind.equals(islandGroup.findIslandTileByID(islandTileToFind.getID())));
    }
}