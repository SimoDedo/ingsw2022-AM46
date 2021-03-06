package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * As the name suggests, this class creates Characters according to their provided ID.
 */
public class CharacterFactory {

    /**
     * Constructor for the CharacterFactory.
     */
    public CharacterFactory() {}

    /**
     * Method which creates a Character according to the given ID.
     * @param ID the ID of the Character to create
     * @return the Character object, created with the correct parameters
     */
    public AbstractCharacter create(int ID, Bag bag){
        AbstractCharacter character = null;
        List<RequestParameter> requestParameters = new ArrayList<>();
        switch (ID) {
            case 1 -> {
                requestParameters.add(RequestParameter.STUDENT_CARD);
                requestParameters.add(RequestParameter.ISLAND);
                character = new StudentMoverCharacter(1, 1, 1, 4, requestParameters);
                for (int i = 0; i < 4; i++)
                    if (bag != null)
                        ((StudentMoverCharacter) character).placePawn(bag.draw());
            }
            case 2 -> character = new StrategyCharacter(2, 2, requestParameters);
            case 3 -> {
                requestParameters.add(RequestParameter.ISLAND);
                character = new StrategyCharacter(3, 3, requestParameters);
            }
            case 4 -> character = new StrategyCharacter(4, 1, requestParameters);
            case 5 -> {
                requestParameters.add(RequestParameter.ISLAND);
                character = new NoEntryCharacter(5, 2, 4, requestParameters);
            }
            case 6 -> character = new StrategyCharacter(6, 3, requestParameters);
            case 7 -> {
                requestParameters.add(RequestParameter.STUDENT_CARD);
                requestParameters.add(RequestParameter.STUDENT_ENTRANCE);
                character = new StudentMoverCharacter(7, 1, 3, 6, requestParameters);
                for (int i = 0; i < 6; i++)
                    if (bag != null)
                        ((StudentMoverCharacter) character).placePawn(bag.draw());
            }
            case 8 -> character = new StrategyCharacter(8, 2, requestParameters);
            case 9 -> {
                requestParameters.add(RequestParameter.COLOR);
                character = new StrategyCharacter(9, 3, requestParameters);
            }
            case 10 -> {
                requestParameters.add(RequestParameter.STUDENT_ENTRANCE);
                requestParameters.add(RequestParameter.STUDENT_DINING_ROOM);
                character = new StudentMoverCharacter(10, 1, 2, 0, requestParameters);
            }
            case 11 -> {
                requestParameters.add(RequestParameter.STUDENT_CARD);
                character = new StudentMoverCharacter(11, 2, 1, 4, requestParameters);
                for (int i = 0; i < 4; i++)
                    if (bag != null)
                        ((StudentMoverCharacter) character).placePawn(bag.draw());
            }
            case 12 -> {
                requestParameters.add(RequestParameter.COLOR);
                character = new StudentMoverCharacter(12, 3, 1, 0, requestParameters);
            }
        }
        return character;
    }
}
