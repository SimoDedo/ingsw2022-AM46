package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {


    /**
     * Checks that the checkDesperate method signals when an assistant has already been played this round
     */
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
        assertFalse(p0.checkDesperate(playedThisRound),
                "checkDesperate has improperly signaled that all the cards in the player's hand have already been played this round");

        playedThisRound.add(new AssistantCard(10, 5));
        assertTrue(p0.checkDesperate(playedThisRound),
                "checkDesperate has improperly signaled that there is at least one card in the player's hand that has not been played yet this round");
    }

    /**
     * Verifies that students are removed as expected from the table
     */
    @Test
    public void removeNFromDRTest(){
        PlayerConfig pc = new PlayerConfig(2);
        Bag bag = new Bag();
        pc.setBag(bag);
        Player p0 = new Player("name", TowerColor.BLACK, true, pc);
        p0.pickWizard(WizardType.MAGE);
        try {
            for (int i = 0; i < 5; i++) {
                p0.addToDR(new Student(Color.BLUE, p0.getTable(Color.BLUE)));
            }
        } catch (FullTableException e) { fail("table improperly signaling it is full"); }
        List<Student> removed = p0.removeNFromDR(3, Color.BLUE);
        assertEquals(removed.size(), 3, "wrong number of students have been removed");
        assertEquals(p0.getScore(Color.BLUE), 2, "wrong number of students in table after removal");
        removed = p0.removeNFromDR(10, Color.BLUE);
        assertEquals(removed.size(), 2, "requesting to remove more students that there actually are in the table does not ignore the extra students");
        assertEquals(p0.getScore(Color.BLUE), 0, "requesting to remove more students that there actually are does not remove all students from the table");
    }
}
