package it.polimi.ingsw.GameModel.Characters.Dynamite;

import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.ArrayList;
import java.util.List;

public class CharacterFactoryDynamite {

    /**
     * Constructor for the CharacterFactory.
     */
    public CharacterFactoryDynamite() {}

    /**
     * Method which creates a Character
     * @param ID The ID of the Character to create
     * @return The Character object
     */
    public CharacterDynamite create(int ID){
        CharacterDynamite character = null;
        List<RequestParameters> requestParameters = new ArrayList<>();
        switch (ID){
            case 1:
                requestParameters.add(RequestParameters.STUDCARD);
                requestParameters.add(RequestParameters.ISLAND);
                character = new StudentMoverCharacterDynamite(1, 1, 1, 4, requestParameters);
                break;
            case 2:
                character = new StrategyCharacterDynamite(2, 2, requestParameters);
                break;
            case 3:
                requestParameters.add(RequestParameters.ISLAND);
                character = new StrategyCharacterDynamite(3, 3, requestParameters);
                break;
            case 4:
                character = new StrategyCharacterDynamite(4,1, requestParameters);
                break;
            case 5:
                requestParameters.add(RequestParameters.ISLAND);
                character = new NoEntryCharacterDynamite(5, 2, 4, requestParameters);
                break;
            case 6:
                character = new StrategyCharacterDynamite(6, 3, requestParameters);
                break;
            case 7:
                requestParameters.add(RequestParameters.STUDCARD);
                requestParameters.add(RequestParameters.STUDENTRANCE);
                character = new StudentMoverCharacterDynamite(7, 1, 3, 6, requestParameters);
                break;
            case 8:
                character = new StrategyCharacterDynamite(8, 2, requestParameters);
                break;
            case 9:
                requestParameters.add(RequestParameters.COLOR);
                character = new StrategyCharacterDynamite(9, 3, requestParameters);
                break;
            case 10:
                requestParameters.add(RequestParameters.STUDENTRANCE);
                requestParameters.add(RequestParameters.STUDDININGROOM);
                character = new StudentMoverCharacterDynamite(10, 1, 2, 0, requestParameters);
                break;
            case 11:
                requestParameters.add(RequestParameters.STUDCARD);
                character = new StudentMoverCharacterDynamite(11, 2, 1, 4, requestParameters);
                break;
            case 12:
                requestParameters.add(RequestParameters.COLOR);
                character = new StudentMoverCharacterDynamite(12, 3, 1, 0, requestParameters);
                break;
        }
        return character;
    }
}
