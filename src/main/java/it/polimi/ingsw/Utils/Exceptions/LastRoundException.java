package it.polimi.ingsw.Utils.Exceptions;

/**
 * Exception thrown to the controller when either the last student was drawn or any player played his last assistant
 */
public class LastRoundException extends RuntimeException{
    public LastRoundException(){
        super();
    }

    public LastRoundException(String msg){
        super(msg);
    }
}
