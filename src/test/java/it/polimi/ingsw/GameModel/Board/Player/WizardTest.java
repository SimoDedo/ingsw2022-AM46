package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class WizardTest {

    @Test
    public void playAssistantTest(){
        Wizard wiz = new Wizard(WizardType.MAGE);
        for(int i = 0; i < 10; i ++) {
            try {
                assert (wiz.playAssistant(i).getMovePower() == Math.floor(i / 2));
            } catch (GameOverException e){}
        }
        Assertions.assertThrows(NoSuchElementException.class, () -> wiz.playAssistant(1));

    }
}
