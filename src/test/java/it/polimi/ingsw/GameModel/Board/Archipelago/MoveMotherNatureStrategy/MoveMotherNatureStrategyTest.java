package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the standard strategy for MoveMotherNature
 */
class MoveMotherNatureStrategyTest {

    /**
     * Tests that method correctly throws exception if movement is not allowed
     */
    @Test
    void moveMotherNatureException() {
        Archipelago archipelago = new Archipelago();

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int idxEndIG =  idxStartIG + 6 > 11 ? idxStartIG + 6 - 12 : idxStartIG + 6;
        int moveCount = 6;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertThrows(IllegalArgumentException.class,() -> archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount-1),
                "no exception raised on illegal mother nature movement");
    }

    /**
     * Tests that method correctly lets player move MotherNature
     */
    @Test
    void moveMotherNature() {
        Archipelago archipelago = new Archipelago();

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int moveCount = 6;
        int idxEndIG =  idxStartIG + moveCount > 11 ? idxStartIG + moveCount - 12 : idxStartIG + moveCount;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertEquals(archipelago.getMotherNatureIslandGroupIndex(), idxEndIG, "unexpected mother nature location index after move");
    }

    /**
     * Tests that method correctly lets player move MotherNature when making an entire loop if capable
     */
    @Test
    void moveMotherNatureLoop() {
        Archipelago archipelago = new Archipelago();

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int moveCount = 12;
        int idxEndIG =  idxStartIG + moveCount > 11 ? idxStartIG + moveCount - 12 : idxStartIG + moveCount;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertEquals(archipelago.getMotherNatureIslandGroupIndex(), idxEndIG, "unexpected mother nature location index after move");

        int idxStartIG2 = archipelago.getMotherNatureIslandGroupIndex();
        int moveCount2 = 2;
        int idxEndIG2 =  idxStartIG2 + 12 > 11 ? idxStartIG2 + 12 - 12 : idxStartIG2 + 12;
        assertThrows(IllegalArgumentException.class,() -> archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG2).get(0), moveCount2),
                "no exception raised on illegal mother nature movement");
    }
}