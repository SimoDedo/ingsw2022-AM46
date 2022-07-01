package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.AssistantCard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tests for the TurnManager class.
 */
public class TurnManagerTest {

    /**
     * Tests that players are stored in the correct order. Used in other tests (?)
     */
    @Test
    public void testStartingPlanningOrder() {
        Bag bag = new Bag();
        bag.fillRemaining();
        PlayerConfig config = new PlayerConfig(3);
        config.setBag(bag);
        Player player1 = new Player("1-pietro", TowerColor.WHITE, true, config);
        Player player2 = new Player("2-simo", TowerColor.GREY, true, config);
        Player player3 = new Player("3-greg", TowerColor.BLACK, true, config);
        TurnManager turnManager = new TurnManager();
        turnManager.addPlayerClockwise(player1);
        turnManager.addPlayerClockwise(player2);
        turnManager.addPlayerClockwise(player3);
        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); //phase now set to planning with planning order set up
        String nick1 = turnManager.getCurrentPlayer().getNickname();
        turnManager.nextTurn();
        String nick2 = turnManager.getCurrentPlayer().getNickname();
        turnManager.nextTurn();
        String nick3 = turnManager.getCurrentPlayer().getNickname();
        assertNotSame(nick1, nick2, "turn 1 and turn 2 share the same current player");
        assertNotSame(nick2, nick3, "turn 2 and turn 3 share the same current player");
        assertNotSame(nick3, nick1, "turn 3 and turn 1 share the same current player");
    }

    /**
     * verifies that players are picked in the correct order (depending on what assistant they played this round)
     */
    @Test
    public void testDeterminePlanningOrder() {
        Bag bag = new Bag();
        bag.fillRemaining();
        PlayerConfig config = new PlayerConfig(3);
        config.setBag(bag);
        Player player1 = new Player("1-pietro", TowerColor.WHITE, true, config);
        Player player2 = new Player("2-simo", TowerColor.GREY, true, config);
        Player player3 = new Player("3-greg", TowerColor.BLACK, true, config);
        TurnManager turnManager = new TurnManager();

        turnManager.addPlayerClockwise(player2);
        turnManager.addPlayerClockwise(player3);
        turnManager.addPlayerClockwise(player1);

        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); //planning phase

        Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();
        cardsPlayedThisRound.put(player3, new AssistantCard(4, 10));
        cardsPlayedThisRound.put(player1, new AssistantCard(2, 10));
        cardsPlayedThisRound.put(player2, new AssistantCard(1, 10));
        turnManager.determineActionOrder(cardsPlayedThisRound);
        turnManager.nextPhase(); //action phase

        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // 2nd planning phase

        assertSame(player2.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 1");
        turnManager.nextTurn();
        assertSame(player3.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 2");
        turnManager.nextTurn();
        assertSame(player1.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 3");
    }

    /**
     *
     */
    @Test
    public void testDetermineActionOrder() {
        Bag bag = new Bag();
        bag.fillRemaining();
        PlayerConfig config = new PlayerConfig(3);
        config.setBag(bag);
        Player player1 = new Player("1-pietro", TowerColor.WHITE, true, config);
        Player player2 = new Player("2-simo", TowerColor.GREY, true, config);
        Player player3 = new Player("3-greg", TowerColor.BLACK, true, config);
        TurnManager turnManager = new TurnManager();

        turnManager.addPlayerClockwise(player2);
        turnManager.addPlayerClockwise(player3);
        turnManager.addPlayerClockwise(player1);

        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // planning phase: play assistant cards

        Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();
        cardsPlayedThisRound.put(player3, new AssistantCard(4, 10));
        cardsPlayedThisRound.put(player1, new AssistantCard(2, 10));
        cardsPlayedThisRound.put(player2, new AssistantCard(1, 10));
        turnManager.determineActionOrder(cardsPlayedThisRound);
        turnManager.nextPhase(); // action phase

        assertSame(player2.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 1");
        turnManager.nextTurn();
        assertSame(player1.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 2");
        turnManager.nextTurn();
        assertSame(player3.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 3");
    }

    /**
     * testing that order is chosen properly even if multiple players play an assistant with the same turn order weight
     */
    @Test
    public void desperateActionOrder() {
        Bag bag = new Bag();
        bag.fillRemaining();
        PlayerConfig config = new PlayerConfig(3);
        config.setBag(bag);
        Player player1 = new Player("1-pietro", TowerColor.WHITE, false, config);
        Player player2 = new Player("Simo", TowerColor.WHITE, true, config);
        Player player3 = new Player("Greg", TowerColor.BLACK, false, config);
        Player player4 = new Player("Margara <3", TowerColor.BLACK, true, config);
        Player player5 = new Player("Ceruti", TowerColor.GREY, true, config);

        TurnManager turnManager = new TurnManager();
        turnManager.addPlayerClockwise(player5);
        turnManager.addPlayerClockwise(player4);
        turnManager.addPlayerClockwise(player3);
        turnManager.addPlayerClockwise(player2);
        turnManager.addPlayerClockwise(player1);

        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // planning phase: play assistant cards

        Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();
        cardsPlayedThisRound.put(player1, new AssistantCard(4, 5));
        cardsPlayedThisRound.put(player2, new AssistantCard(2, 6));
        cardsPlayedThisRound.put(player3, new AssistantCard(2, 3));
        cardsPlayedThisRound.put(player4, new AssistantCard(2, 7));
        cardsPlayedThisRound.put(player5, new AssistantCard(4, 5));

        turnManager.determineActionOrder(cardsPlayedThisRound);
        turnManager.nextPhase(); // action phase

        assertSame(player2.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 1");
        turnManager.nextTurn();
        assertSame(player3.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 2");
        turnManager.nextTurn();
        assertSame(player4.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 3");
        turnManager.nextTurn();
        assertSame(player1.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 4");
        turnManager.nextTurn();
        assertSame(player5.getNickname(), turnManager.getCurrentPlayer().getNickname(), "unexpected player for turn 5");
    }

}