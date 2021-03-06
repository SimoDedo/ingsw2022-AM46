package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.Utils.Enum.WizardType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that models a deck of assistants of certain wizard type.
 * It offers methods to observe the remaining cards and play them.
 */
public class Wizard implements Serializable {

    private final WizardType type;

    final int deckSize = 10;
    private final List<AssistantCard> deck = new ArrayList<>();

    /**
     * initializes the deck of assistants for this wizard
     * @param type wizard card back
     */
    public Wizard(WizardType type) {
        this.type = type;
        for(int i = 1; i <= deckSize; i++){
            deck.add(new AssistantCard(i,(i+1)/2));
        }
    }


    /**
     * @param assistantID unique ID of the assistant to be played
     * @return the assistant with the corresponding ID
     * @throws NoSuchElementException if this wizard's deck does not contain an assistant with specified ID
     */
    public AssistantCard playAssistant(int assistantID) throws NoSuchElementException {
        AssistantCard assistant = deck.stream().filter(card -> card.getID() == assistantID).
                findAny().orElseThrow(() -> new NoSuchElementException("Already played this assistant in previous turn"));
        deck.removeIf(card -> card.getID() == assistantID);
        return assistant;
    }

    public WizardType getType() {
        return type;
    }

    public List<AssistantCard> getDeck() {
        return deck;
    }

    /**
     * Returns a list of cards that weren't yet played (thus to be shown to the player)
     * @return a list of card IDs
     */
    public List<Integer> getCardsLeft(){
        List<Integer> cardsLeft = new ArrayList<>();
        for (AssistantCard assistantCard : deck)
            cardsLeft.add(assistantCard.getID());
        return cardsLeft;
    }
}
