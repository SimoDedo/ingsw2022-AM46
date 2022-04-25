package it.polimi.ingsw.Utils.Exceptions;

public class GameOverException extends RuntimeException{
    public GameOverException(){ super(); }

    public GameOverException(String msg){ super(msg); }
}
