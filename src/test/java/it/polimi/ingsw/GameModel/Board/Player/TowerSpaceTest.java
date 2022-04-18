package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TowerSpaceTest {


    /**
     * Verifies that the takeTower and getTowersPlaced methods work as expected
     */
    @Test
    public void takeTowerTest(){
        Player p0 = new Player();
        TowerSpace ts = new TowerSpace(p0, 10, TowerColor.BLACK);
        ts.takeTower();
        ts.takeTower();
        ts.takeTower();
        assertEquals(ts.getTowersPlaced(),3, "wrong number of towers in container");

    }
}
