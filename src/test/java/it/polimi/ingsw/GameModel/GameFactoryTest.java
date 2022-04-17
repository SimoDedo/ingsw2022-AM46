package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {

    @Test
    void createNormal() {
        Map<String, TowerColor> teamComposition = new LinkedHashMap<>();
        teamComposition.put("pietro", TowerColor.WHITE);
        teamComposition.put("simo", TowerColor.WHITE);
        teamComposition.put("greg", TowerColor.BLACK);
        teamComposition.put("pirovano", TowerColor.BLACK);

        GameFactory factory = new GameFactory();
        Game normal = factory.create(4, GameMode.NORMAL);
        assertSame(normal.getClass(), Game.class);
    }

    @Test
    void createExpert() {
        Map<String, TowerColor> teamComposition = new LinkedHashMap<>();
        teamComposition.put("pietro", TowerColor.WHITE);
        teamComposition.put("simo", TowerColor.WHITE);
        teamComposition.put("greg", TowerColor.BLACK);
        teamComposition.put("pirovano", TowerColor.BLACK);

        GameFactory factory = new GameFactory();
        Game expert = factory.create(4, GameMode.EXPERT);
        assertSame(expert.getClass(), GameExpert.class);
    }

}