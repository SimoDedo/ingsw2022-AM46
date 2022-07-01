package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.WizardType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

/**
 * Tests for the Wizard class.
 */
public class WizardTest {

    /**
     * Checks that the same assistant can't be played twice and that all assistants are created with the correct values
     */
    @Test
    public void playAssistantTest(){
        Wizard wiz = new Wizard(WizardType.MAGE);
        for(int i = 1; i <= 10; i ++) {
            assertEquals(wiz.playAssistant(i).getMovePower(), (i + 1)/2, "assistant effectively played is not what was requested");

        }
        assertThrows(NoSuchElementException.class, () -> wiz.playAssistant(1), "this assistant has already been played");


    }

    /**
     * 80 % coverage :)
     */
    @Test
    public void testGetters(){
        Wizard wiz = new Wizard(WizardType.MAGE);
        assertEquals(wiz.getType(), WizardType.MAGE, "unexpected wizard type returned");

    }
}
