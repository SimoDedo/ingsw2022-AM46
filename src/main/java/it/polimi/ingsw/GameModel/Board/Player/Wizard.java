package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Game;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Wizard {

    private final WizardType type;

    int deckSize = 10;
    private final List<AssistantCard> deck = new ArrayList<>();

    /**
     * initializes the deck of assistants for this wizard
     * @param type wizard card back
     */
    public Wizard(WizardType type) {
        this.type = type;
        for(int i = 0; i < deckSize; i++){
            deck.add(new AssistantCard(i, i/2));
        }
    }


    /**
     * @param assistantID unique ID of the assistant to be played
     * @return the assistant with the corresponding ID
     * @throws NoSuchElementException if this wizard's deck does not contain an assistant with specified ID
     */
    public AssistantCard playAssistant(int assistantID) throws NoSuchElementException, GameOverException {
        if(deck.size() == 0){ throw new GameOverException(); }
        AssistantCard assistant = deck.stream().filter(card -> card.getID() == assistantID).
                findAny().orElseThrow(NoSuchElementException::new);
        deck.removeIf(card -> card.getID() == assistantID);
        return assistant;
    }

    public WizardType getType() {
        return type;
    }

    public List<AssistantCard> getDeck() {
        return deck;
    }
}
