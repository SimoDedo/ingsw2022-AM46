package it.polimi.ingsw.GameModel.BoardElements;

import it.polimi.ingsw.GameModel.Board.Player.TowerSpace;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TowerContainerTest {
    /**
     * Tests that placePawn doesn't place pawn if full (at creation TowerSpace is full)
     */
    @Test
    void placePawn() {
        TowerSpace towerSpace = new TowerSpace(null, 8, TowerColor.BLACK);
        Tower tower = new Tower(TowerColor.BLACK, null);
        assertThrows(IllegalArgumentException.class, () -> towerSpace.placePawn(tower), "tower was added even if container was full");
    }

    /**
     * Tests that removePawn actually removes pawns inside container. It checks by counting the pawns before and after removal
     */
    @Test
    void removePawn() {
    }

}