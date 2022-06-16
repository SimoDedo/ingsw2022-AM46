package it.polimi.ingsw.Utils.Exceptions;

/**
 * Exception thrown whenever a student is being added to a table that is already full.
 */
public class FullTableException extends RuntimeException{
    public FullTableException(String message){super(message);}
}
