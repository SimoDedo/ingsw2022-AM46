package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.Utils.Enum.Color;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CharacterTestTest {

    @RepeatedTest(20)
    void testLambda() {
        Archipelago archipelago = new Archipelago();
        Bag bag = new Bag();
        bag.fillRemaining();
        CharacterTest characterTest = new CharacterTest(null, 4);
        characterTest.placePawns(bag.drawN(4));
        LambdaTest lambdaTest = new LambdaTest(archipelago, bag, characterTest);
        List<Integer> list = new ArrayList<>();

        int ID = characterTest.getPawnsIDs().get(0);
        list.add(ID);
        list.add(archipelago.getIslandTilesIDs().get(0).get(0));
        characterTest.testLambda(lambdaTest.C1, list);
        assertTrue(archipelago.getStudentsIDs().get(0).get(0).equals(ID));
    }
}