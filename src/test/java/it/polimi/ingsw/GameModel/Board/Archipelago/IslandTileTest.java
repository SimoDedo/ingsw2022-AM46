package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;



/**
 * Tests for the IslandTile Class
 */
class IslandTileTest {

    /**
     * Tests that MotherNature is actually placed
     */
    @Test
    void placeMotherNature() {
        IslandTile islandTile = new IslandTile(null, false, null);
        islandTile.placeMotherNature(new MotherNature(islandTile));
        assertTrue(islandTile.hasMotherNature(), "mother nature not found in island tile after placing");
    }

    /**
     * Tests that mother nature is initially given and then removed
     */
    @Test
    void removeMotherNature() {
        IslandTile islandTile = new IslandTile(null, true, null);
        assertTrue(islandTile.hasMotherNature(), "mother nature not found in starting island tile");
        islandTile.removeMotherNature();
        assertFalse(islandTile.hasMotherNature(), "mother nature still in tile after removal");
    }

    /**
     * Checks that Tower is correctly assigned when no Tower was previously held
     */
    @Test
    void swapTowerFromNull() {
        IslandTile islandTile = new IslandTile(null, true, null);
        Tower tower = new Tower(TowerColor.BLACK, null);
        islandTile.swapTower(tower);
        assertEquals((int) islandTile.getTowerID(), tower.getID(), "unexpected tower found in islandTile after swap");
    }

    /**
     * Checks tower is correctly swapped through the color
     */
    @Test
    void swapTower() {
        IslandTile islandTile = new IslandTile(null, true, null);
        Tower tower = new Tower(TowerColor.BLACK, null);
        Tower tower1 = new Tower(TowerColor.WHITE, null);
        islandTile.swapTower(tower);
        islandTile.swapTower(tower1);
        assertSame(islandTile.getTowerColor(), TowerColor.WHITE, "unexpected tower color found in islandTile after swap");
    }

    /**
     * Checks if tower is correctly swapped even if they have the same color (should not happen in a game)
     */
    @Test
    void swapTowerSameColor(){
        IslandTile islandTile = new IslandTile(null, true, null);
        Tower tower = new Tower(TowerColor.BLACK, null);
        Tower tower1 = new Tower(TowerColor.BLACK, null);
        islandTile.swapTower(tower);
        islandTile.swapTower(tower1);
        assertTrue(islandTile.getTowerID().equals(tower1.getID()) && ! islandTile.getTowerID().equals(tower.getID()) && islandTile.getTowerColor() == tower.getColor(),
        "unexpected swap results when swapping towers of the same color");
    }

    /**
     * Checks that after placing Students you can correctly count them
     */
    @Test
    void countInfluence() {
        IslandTile islandTile = new IslandTile(null, true, null);
        List<Student> students = new ArrayList<>();
        students.add(new Student(Color.BLUE, islandTile));
        students.add(new Student(Color.BLUE, islandTile));
        students.add(new Student(Color.BLUE, islandTile));
        for(Student student : students)
            islandTile.placePawn(student);
        assertEquals(3, islandTile.countInfluence(Color.BLUE), "unexpected influence counted in islandTile");
    }

    /**
     * Checks that after placing Students you can correctly count them
     */
    @Test
    void countInfluenceZero() {
        IslandTile islandTile = new IslandTile(null, true, null);
        assertEquals(0, islandTile.countInfluence(Color.BLUE), "unexpected influence counted in islandTile");
    }
}