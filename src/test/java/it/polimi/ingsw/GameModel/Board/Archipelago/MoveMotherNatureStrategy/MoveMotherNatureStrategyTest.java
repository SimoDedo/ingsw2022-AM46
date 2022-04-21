package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;

class MoveMotherNatureStrategyTest {

    /**
     * Tests that method correctly throws exception if movement is not allowed
     * @throws InvalidObjectException not tested
     */
    @Test
    void moveMotherNatureException() throws InvalidObjectException {
        Archipelago archipelago = new Archipelago();

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int idxEndIG =  idxStartIG + 6 > 11 ? idxStartIG + 6 - 12 : idxStartIG + 6;
        int moveCount = 6;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertThrows(InvalidObjectException.class,() -> archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount-1),
                "no exception raised on illegal mother nature movement");
    }

    /**
     * Tests that method correctly lets player move MotherNature
     * @throws InvalidObjectException not tested
     */
    @Test
    void moveMotherNature() throws InvalidObjectException {
        Archipelago archipelago = new Archipelago();

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int idxEndIG =  idxStartIG + 6 > 11 ? idxStartIG + 6 - 12 : idxStartIG + 6;
        int moveCount = 6;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertEquals(archipelago.getMotherNatureIslandGroupIndex(), idxEndIG, "unexpected mother nature location index after move");
    }
}