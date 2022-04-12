package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.List;

/**
 * This subclass of AbstractCharacter deals with setting strategies inside the GameModel.
 */
public class StrategyCharacter extends AbstractCharacter {

    public StrategyCharacter(int ID, int cost, List<RequestParameter> requestParameters) {
        super(ID, cost, requestParameters);
    }

}
