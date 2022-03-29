package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import org.junit.jupiter.api.Test;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MotherNatureStrategyTest {

    /**
     * Tests that StandardMotherNatureStrategy correctly moves motherNature
     */
    @Test
    public void standardStrategyTest(){ //because (rightfully so) IslandTiles and IslandGroups are hidden in Archipelago
                                        // and there are no getters, to test this method we need to create various stuff
        MotherNatureStrategy motherNatureStrategy = new StandardMotherNatureStrategy();
        List<IslandGroup> islandGroups = new ArrayList<IslandGroup>(); //Simulates the IslandGroups in archipelago
        IslandTile islandTileDestination = new IslandTile(null, -1, false, null); //Declared here to have reference to insert in method
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandTiles.add(islandTileDestination);
        int indexOfStartingIslandGroup = 9;
        int indexOfDestinationIslandGroup = 4; //
        int moveCount = 6;
        for (int i = 0; i < 11; i++) {
            boolean cond = i == indexOfStartingIslandGroup? true : false;
            islandGroups.add(new IslandGroup(cond));
        }
        islandGroups.get(indexOfDestinationIslandGroup).addIslandTilesAfter(islandTiles);
        try {
            motherNatureStrategy.moveMotherNature(islandGroups.get(indexOfStartingIslandGroup).getMotherNatureTile(), islandTileDestination, moveCount,islandGroups);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
        assertTrue(islandTileDestination.hasMotherNature() && islandGroups.get(indexOfDestinationIslandGroup).getMotherNatureTile().equals(islandTileDestination));
    }

    /**
     * Tests that StandardMotherNatureStrategy correctly throws exceptions when queried with impossible moves (corner cases, 1 less move available and 0 moves available)
     */
    @Test
    public void standardStrategyTestException(){
        MotherNatureStrategy motherNatureStrategy = new StandardMotherNatureStrategy();
        List<IslandGroup> islandGroups = new ArrayList<IslandGroup>();
        IslandTile islandTileDestination = new IslandTile(null, -1, false, null);
        List<IslandTile> islandTiles = new ArrayList<IslandTile>();
        islandTiles.add(islandTileDestination);
        int indexOfStartingIslandGroup = 9;
        int indexOfDestinationIslandGroup = 4;
        int moveCount = 5;
        for (int i = 0; i < 11; i++) {
            boolean cond = i == indexOfStartingIslandGroup? true : false;
            islandGroups.add(new IslandGroup(cond));
        }
        islandGroups.get(indexOfDestinationIslandGroup).addIslandTilesAfter(islandTiles);
        assertThrows(InvalidObjectException.class,
                () -> {motherNatureStrategy.moveMotherNature(islandGroups.get(indexOfStartingIslandGroup).getMotherNatureTile(), islandTileDestination, moveCount,islandGroups);});
        assertThrows(InvalidObjectException.class,
                () -> {motherNatureStrategy.moveMotherNature(islandGroups.get(indexOfStartingIslandGroup).getMotherNatureTile(), islandTileDestination, 0,islandGroups);});
    }


    //Other strategies tests

}