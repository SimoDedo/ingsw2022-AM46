package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;
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
        assertTrue(islandGroup.hasMotherNature(), "mother nature not found in starting islandGroup");
    }

    /**
     * Tests that methods returns an IslandTile containing MotherNature
     */
    @Test
    void getMotherNatureTile() {
        IslandGroup islandGroup = new IslandGroup(true);
        assertTrue(islandGroup.getMotherNatureTile().hasMotherNature(),
                "islandTile returned by getMotherNatureTile() does not contain mother nature");
    }

    /**
     * Test that counting influence works when IslandGroup contains multiple IslandTiles
     */
    @Test
    void countInfluence() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
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
        assertEquals(7, islandGroup.countInfluence(Color.PINK), "unexpected influence score in islandGroup");
        assertEquals(3, islandGroup.countInfluence(Color.RED), "unexpected influence score in islandGroup");
        assertEquals(0, islandGroup.countInfluence(Color.BLUE), "unexpected influence score in islandGroup");
    }

    /**
     *  Tests that team correctly conquers
     * @throws GameOverException not used
     */
    @Test
    void conquer() throws GameOverException {
        IslandGroup islandGroup = new IslandGroup(true);
        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        PlayerList players = new PlayerList();
        players.add(new Player("Simo", TowerColor.BLACK, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Greg", TowerColor.BLACK, false, gameConfig.getPlayerConfig()));
        players.add(new Player("Pirovano", TowerColor.WHITE, true, gameConfig.getPlayerConfig()));
        players.add(new Player("Ceruti", TowerColor.WHITE, false, gameConfig.getPlayerConfig()));

        islandGroup.conquer(players.getTowerHolder(TowerColor.BLACK));
        assertEquals(islandGroup.getTowerColor(), TowerColor.BLACK, "unexpected (or none) tower found in islandGroup");
        islandGroup.conquer(players.getTowerHolder(TowerColor.WHITE));
        assertEquals(islandGroup.getTowerColor(), TowerColor.WHITE, "unexpected (or none) tower found in islandGroup");
        assertFalse(islandGroup.conquer(players.getTowerHolder(TowerColor.WHITE)), "islandGroup conquered by team who already owned it");
    }

    /**
     * Ensures IslandGroup has no IslandTiles after operation
     */
    @Test
    void removeIslandTiles() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = islandGroup.removeIslandTiles();
        assertNotNull(islandTiles.get(0), "islandGroup returned no removed tiles");
        assertTrue(islandTiles.get(0).hasMotherNature(), "starting island tile has no mother nature");
        assertFalse(islandGroup.hasIslandTile(islandTiles.get(0)), "islandGroup still contains removed island tile");
    }

    /**
     * Tests if method correctly puts IslandTiles after those already present
     */
    @Test
    void addIslandTilesBefore() {
        IslandGroup islandGroup1 = new IslandGroup(true);
        IslandGroup islandGroup2 = new IslandGroup(false);
        IslandGroup islandGroup3 = new IslandGroup(false);
        List<IslandTile> islandTiles;
        islandGroup2.addIslandTilesBefore(islandGroup1.removeIslandTiles());
        islandGroup3.addIslandTilesBefore(islandGroup2.removeIslandTiles());
        islandTiles = islandGroup3.removeIslandTiles();
        assertTrue(islandTiles.get(0).hasMotherNature(), "starting island tile has no mother nature");
        assertEquals(3, islandTiles.size(), "unexpected number of islandTiles removed from islandGroup");
    }

    /**
     * Tests if method correctly puts IslandTiles after those already present
     */
    @Test
    void addIslandTilesAfter() {
        IslandGroup islandGroup1 = new IslandGroup(true);
        IslandGroup islandGroup2 = new IslandGroup(false);
        IslandGroup islandGroup3 = new IslandGroup(false);
        List<IslandTile> islandTiles;
        islandGroup2.addIslandTilesAfter(islandGroup1.removeIslandTiles());
        islandGroup3.addIslandTilesAfter(islandGroup2.removeIslandTiles());
        islandTiles = islandGroup3.removeIslandTiles();
        assertTrue(islandTiles.get(2).hasMotherNature(), "starting island tile has no mother nature");
        assertEquals(3, islandTiles.size(), "unexpected number of islandTiles removed from islandGroup");
    }

    /**
     * Adds IslandTile to an IslandGroup and tests if methods returns true
     */
    @Test
    void hasIslandTile() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        islandTiles.add(new IslandTile(null, false,null ));
        islandGroup.addIslandTilesBefore(islandTiles);
        assertTrue(islandGroup.hasIslandTile(islandTiles.get(0)), "islandTile not found in containing islandGroup");
    }

    /**
     * Tests that method correctly returns the color of the tower placed in the IslandGroup
     */
    @Test
    void getTowerColor() {
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        islandTiles.add(new IslandTile(null, false,null ));
        islandTiles.get(0).swapTower(new Tower(TowerColor.BLACK, null));
        islandGroup.addIslandTilesBefore(islandTiles);
        assertSame(islandGroup.getTowerColor(), TowerColor.BLACK, "unexpected tower color in islandGroup");
    }

    /**
     * Tests that method actually finds the right student
     */
    @Test
    void findStudentByID(){
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        IslandTile islandTile = new IslandTile(null, true, null);
        islandTile.placePawn(new Student(Color.GREEN, null));
        islandTile.placePawn(new Student(Color.PINK, null));
        islandTile.placePawn(new Student(Color.RED, null));
        islandTile.placePawn(new Student(Color.BLUE, null));
        Student studentToFind = new Student(Color.YELLOW, null);
        islandTile.placePawn(studentToFind);
        islandTiles.add(islandTile);
        islandGroup.addIslandTilesBefore(islandTiles);
        assertEquals(studentToFind, islandGroup.getStudentByID(studentToFind.getID()), "unexpected student returned by id lookup");
    }

    /**
     * Tests that method actually finds the right IslandTile
     */
    @Test
    void findIslandTileByID(){
        IslandGroup islandGroup = new IslandGroup(true);
        List<IslandTile> islandTiles = new ArrayList<>();
        IslandTile islandTileToFind = new IslandTile(null, true, null);
        IslandTile islandTile1 = new IslandTile(null, true, null);
        islandTiles.add(islandTileToFind);
        islandTiles.add(islandTile1);
        islandGroup.addIslandTilesBefore(islandTiles);
        assertEquals(islandTileToFind, islandGroup.getIslandTileByID(islandTileToFind.getID()), "unexpected islandTile returned by id lookup");
    }
}