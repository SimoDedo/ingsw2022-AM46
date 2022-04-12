package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test strategy C4 for MoveMotherNature
 */
class MoveMotherNatureStrategyC4Test {

    /**
     * Tests that method correctly lets player move 2 additional IslandGroup
     * @throws InvalidObjectException not tested
     */
    @Test
    void moveMotherNature() throws InvalidObjectException {
        Archipelago archipelago = new Archipelago();
        MoveMotherNatureStrategyC4 moveMotherNatureStrategyC4= new MoveMotherNatureStrategyC4();
        archipelago.setMotherNatureStrategy(moveMotherNatureStrategyC4);

        int idxStartIG = archipelago.getMotherNatureIslandGroupIndex();
        int idxEndIG =  idxStartIG + 6 > 11 ? idxStartIG + 6 - 12 : idxStartIG + 6;
        int moveCount = 4;
        archipelago.moveMotherNature(archipelago.getIslandTilesIDs().get(idxEndIG).get(0), moveCount);
        assertEquals(archipelago.getMotherNatureIslandGroupIndex(), idxEndIG);
    }
}