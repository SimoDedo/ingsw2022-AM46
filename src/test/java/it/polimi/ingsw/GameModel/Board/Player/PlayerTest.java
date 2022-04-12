package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {


    @Test
    public void checkDesperateTest(){
        PlayerConfig pc = new PlayerConfig(2);
        Bag bag = new Bag();
        pc.setBag(bag);
        Player p0 = new Player("name", TowerColor.BLACK, true, pc);
        p0.pickWizard(WizardType.MAGE);

        Collection<AssistantCard> playedThisRound = new ArrayList<>();
        for(int i = 1; i <= 9; i++){
            playedThisRound.add(new AssistantCard(i, (i+1)/2));
        }
        assertFalse(p0.checkDesperate(playedThisRound));

        playedThisRound.add(new AssistantCard(10, 5));
        assertTrue(p0.checkDesperate(playedThisRound));
    }
}
