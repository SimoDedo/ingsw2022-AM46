package it.polimi.ingsw.GameModel.Board.Archipelago;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MotherNature class
 */
class MotherNatureTest {

    /**
     * Tests that getIslandTile returns actual IslandTile (which was set with setIslandTIle, testing also that)
     */
    @Test
    void getIslandTile() {
        MotherNature motherNature = new MotherNature(null);
        IslandTile islandTile = new IslandTile(null, false, null);
        islandTile.placeMotherNature(motherNature);
        assertEquals(motherNature.getIslandTile(), islandTile, "mother nature not found in containing islandTile");
    }

}