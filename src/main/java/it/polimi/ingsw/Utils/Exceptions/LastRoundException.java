package it.polimi.ingsw.Utils.Exceptions;

/**
 * Exception thrown when the current turn being played will be the last.
 */
public class LastRoundException extends RuntimeException{
    public LastRoundException(){
        super();
    }

    public LastRoundException(String msg){
        super(msg);
    }
}
