package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.WizardType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

public class WizardTest {

    @Test
    public void playAssistantTest(){
        Wizard wiz = new Wizard(WizardType.MAGE);
        for(int i = 1; i <= 10; i ++) {
            assertEquals(wiz.playAssistant(i).getMovePower(), (i + 1)/2);

        }
        assertThrows(NoSuchElementException.class, () -> wiz.playAssistant(1));


    }
    @Test
    public void testGetters(){
        Wizard wiz = new Wizard(WizardType.MAGE);
        assertEquals(wiz.getType(), WizardType.MAGE);

    }
}
