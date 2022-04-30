package it.polimi.ingsw.Controller;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Enum.Phase;

public class TurnController {
    private Game game;
    private int studentsToMove;
    private int studentsMovedByCurrentPlayer;

    public TurnController(){}

    public void setGame(Game game) {
        this.game = game;
    }

    public void setNumOfPlayers(int numOfPlayers){
        studentsToMove = switch (numOfPlayers) {
            case 2,4 -> 3;
            case 3 -> 4;
            default -> throw new IllegalStateException("There is no phase currently being played");
        };
        studentsMovedByCurrentPlayer = 0;
    }

    public String getCurrentPlayer(){
        return game.getCurrentPlayer();
    }

    public Phase getCurrentPhase(){
        return game.getCurrentPhase();
    }

    /**
     * Starts tracking of turns in game controller
     */
    public void startGame(){
        game.determineFirstRoundOrder();
    }

    /**
     * Called when a student is moved from entrance. It adds one to the counter of the students moved, and returns it.
     * @return the students moved by the current player.
     */
    public int studentMoved(){
        studentsMovedByCurrentPlayer++;
        return studentsMovedByCurrentPlayer;
    }

    public int getStudentsToMove(){
        return studentsToMove;
    }

    /**
     * Changes the turn and resets the students moved by current player (since it just changed)
     * @throws IllegalStateException thrown when all player have taken their turn. Controller catching this
     * should then change phase.
     */
    public void nextTurn() throws IllegalStateException{
        studentsMovedByCurrentPlayer = 0;
        game.nextTurn();
    }

    /**
     * Progresses the game phase and computes the order of players for the coming phase.
     */
    public void nextPhase() {
        game.nextPhase();
        switch (game.getCurrentPhase()){
            case PLANNING -> game.determinePlanningOrder();
            case ACTION -> game.determineActionOrder();
        }

    }
}
