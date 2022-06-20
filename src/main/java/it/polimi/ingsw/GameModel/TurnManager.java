package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Player.AssistantCard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.Phase;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Class that manages the order in which players play their turn. This class determines planning
 * order and action order in the Planning and Action phases of the game.
 *
 */
public class TurnManager implements Serializable {

    private Player currentPlayer = null; // usage will have to be specified thoroughly! is it even needed??

    private Phase currentPhase = Phase.IDLE;

    public final List<Player> planningPlayerList = new ArrayList<>(); //set back to private!

    public  List<Player> actionPlayerList = new ArrayList<>();
    /**
     * Note: "clockwise order" here is the order in which Players choose their Wizard deck. The
     * assignWizard function from Game will fill clockwisePlayerList little by little each time it
     * is called.
     */
    private final List<Player> clockwisePlayerList = new ArrayList<>();

    public TurnManager() {}

    /**
     * Function that fills clockwisePlayerList one player by one. The assignWizard method from
     * Game will call this method each time it is executed.
     * @param player the new player introduced into the game room
     */
    public void addPlayerClockwise(Player player) throws NullPointerException {
        if (player == null) throw new NullPointerException("Parameter is null");
        clockwisePlayerList.add(player);
    }

    /**
     * Function that randomly selects the player who starts the game. They will be the first
     * to choose their Assistant card in the planning phase of the very first round.
     */
    private Player determineRandomPlayer() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, clockwisePlayerList.size());
        return clockwisePlayerList.get(randomNum);
    }

    public void determinePlanningOrder() {
        planningPlayerList.clear();
        if (actionPlayerList.isEmpty()) {
            determineClockwiseOrder(determineRandomPlayer());
        }
        else {
            determineClockwiseOrder(actionPlayerList.get(0));
        }
        currentPlayer = planningPlayerList.get(0);
    }

    public void determineActionOrder(Map<Player, AssistantCard> cardsPlayedThisRound) {
        // I've never hated code written by me this much, but it works somehow.
        // let's refactor it in the future
        actionPlayerList.clear();
        actionPlayerList = cardsPlayedThisRound
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().getTurnOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        currentPlayer = actionPlayerList.get(0);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns current order
     * @return a list of nicknames ordered
     */
    public List<String> getCurrentOrder(){
        List<String> order = new ArrayList<>();
        List<Player> orderToReturn = currentPhase.equals(Phase.PLANNING) ? planningPlayerList : actionPlayerList;
        for(Player player : orderToReturn)
            order.add(player.getNickname());
        return order;
    }

    /**
     * Method that sets the planning order clockwise starting from a given player.
     * @param startingPlayer player from which to start the planning turn
     * @throws NoSuchElementException the parameter startingPlayer is not a valid player
     * inside the game
     */
    public void determineClockwiseOrder(Player startingPlayer) throws NoSuchElementException {
        int startingPos = -1;
        for (int i = 0; i < clockwisePlayerList.size(); i++) {
            if (startingPlayer == clockwisePlayerList.get(i)) startingPos = i;
            if (startingPos >= 0) planningPlayerList.add(clockwisePlayerList.get(i));
        }
        for (int i = 0; i < startingPos; i++) planningPlayerList.add(clockwisePlayerList.get(i));
        if (startingPos == -1) throw new NoSuchElementException("Player is not inside the game");
    }

    /**
     * This method progresses the turn, updating currentPlayer.
     */
    public void nextTurn() throws IllegalStateException {
        List<Player> currentPhaseList = switch (currentPhase) {
            case PLANNING -> planningPlayerList;
            case ACTION -> actionPlayerList;
            default -> throw new IllegalStateException("There is no phase currently being played");
        };
        int prevPlayerIndex = currentPhaseList.indexOf(currentPlayer);
        if (prevPlayerIndex == currentPhaseList.size() - 1) throw new IllegalStateException("This phase has ended");
        else currentPlayer = currentPhaseList.get(prevPlayerIndex + 1);

    }

    /**
     * This method progresses the phase, going from planning to action and vice-versa.
     */
    public void nextPhase() {
        switch (currentPhase) {
            case IDLE, ACTION -> currentPhase = Phase.PLANNING;
            case PLANNING -> currentPhase = Phase.ACTION;
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

}
