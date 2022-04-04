package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import org.junit.jupiter.api.Test;

public class TowerSpaceTest {

    @Test
    public void takeTowerTest(){
        Player p0 = new Player();
        TowerSpace ts = new TowerSpace(p0, 10, TowerColor.BLACK);

        try{
            ts.takeTower();
            ts.takeTower();
            ts.takeTower();
            assert(ts.getTowersPlaced() == 3);
        } catch (GameOverException e){e.printStackTrace();}
    }
}
