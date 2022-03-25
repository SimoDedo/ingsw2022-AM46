package it.polimi.ingsw.Utils;

/**
 * Common interface for the Factory pattern. Specific factories (e.g. Game, Characters) may
 * implement this interface in the future.
 * @param <T>
 */
public interface AbstractFactory<T> {
    T create();
}
