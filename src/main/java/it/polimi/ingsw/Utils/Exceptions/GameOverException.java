package it.polimi.ingsw.Utils.Exceptions;

/**
 * Exception that is thrown whenever the game is over because a team has won.
 */
public class GameOverException extends RuntimeException{
    public GameOverException(){ super(); }

    public GameOverException(String msg){ super(msg); }
}
