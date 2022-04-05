package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.*;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.Utils.Enum.RequestParameters;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Character Factory which given the ID creates Character with the right attributes
 */
public class CharacterFactory {

    /**
     * Bag given to character that need to draw Students
     */
    Bag bag;

    /**
     * List of the teams in the game given to characters that need this knowledge
     */
    PlayerList players;

    /**
     * Constructor for the CharacterFactory
     * @param bag Bag given to character that need to draw Students
     * @param players List of the teams in the game given to characters that need this knowledge
     */
    public CharacterFactory(Bag bag, PlayerList players) {
        this.bag = bag;
        this.players = players;
    }

    /**
     * Method which creates a Character
     * @param ID The ID of the Character to create
     * @return The Character object
     */
    public Character create(int ID){
        Character character = null;
        List<RequestParameters> requestParameters = new ArrayList<>();
        switch (ID){
            case 1 :
                requestParameters.add(RequestParameters.STUDCARD);
                requestParameters.add(RequestParameters.ISLAND);
                character = new MoveCharacter(null, 4, 1,1, 1, requestParameters);
                ((MoveCharacter) character).setBag(bag);
                ((MoveCharacter) character).initialFill();
                break;
            case 2 :
                requestParameters.add(RequestParameters.ISLAND); //FIXME: placeholder while no C&M prof
                character = new ResolveStrategyCharacter(3, 3, requestParameters, new ResolveStrategyC3());
                break;
            case 3 :
                requestParameters.add(RequestParameters.ISLAND);
                character = new ResolveStrategyCharacter(3, 3, requestParameters, new ResolveStrategyC3());
                break;
            case 4 :
                character = new MoveMotherNatureCharacter(4,1,requestParameters,new MoveMotherNatureStrategyC4());
                break;
            case 5 :
                requestParameters.add(RequestParameters.ISLAND);
                character = new NoEntryTileCharacter(5,2,requestParameters);
                break;
            case 6 :
                character = new ResolveStrategyCharacter(6, 3 , requestParameters, new ResolveStrategyC6());
                break;
            case 7 :
                requestParameters.add(RequestParameters.STUDCARD);
                requestParameters.add(RequestParameters.STUDENTRANCE);
                character = new MoveCharacter(null, 6, 7, 1, 3, requestParameters);
                ((MoveCharacter) character).setBag(bag);
                ((MoveCharacter) character).initialFill();
                break;
            case 8 :
                character = new ResolveStrategyCharacter(8, 2, requestParameters, new ResolveStrategyC8());
                break;
            case 9 :
                requestParameters.add(RequestParameters.COLOR);
                character = new ResolveStrategyCharacter(9, 3, requestParameters, new ResolveStrategyC9());
                break;
            case 10 :
                requestParameters.add(RequestParameters.STUDENTRANCE);
                requestParameters.add(RequestParameters.STUDDININGROOM);
                character = new MoveCharacter(null, 0, 10, 1, 2, requestParameters);
                break;
            case 11 :
                requestParameters.add(RequestParameters.STUDCARD);
                character = new MoveCharacter(null, 4, 11, 2, 1, requestParameters);
                ((MoveCharacter) character).setBag(bag);
                ((MoveCharacter) character).initialFill();
                break;
            case 12 :
                requestParameters.add(RequestParameters.COLOR);
                character = new MoveCharacter(null, 0, 12, 3, 1, requestParameters);
                ((MoveCharacter) character).setPlayers(players);
                break;

        }
        return character;
    }
}
