package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.Map;

public class GameExpert extends Game {

    private CoinBag coinBag;

    public GameExpert(GameConfig gameConfig, Map<String, TowerColor> teamComposition) {
        super(gameConfig, teamComposition);
        coinBag = new CoinBag(20);
    }

    //todo: distributeInitialCoins() and useCharacter()
}
