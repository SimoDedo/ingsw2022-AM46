package it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Archipelago.MotherNature;

import java.util.List;

/**
 * Strategy to move MotherNature when no character is activated
 */
public class MoveMotherNatureStrategyStandard implements MoveMotherNatureStrategy {
    /**
     * Moves motherNature to the destination, throws exception if movement is not allowed
     * @param islandTileStarting IslandTile where MotherNature is now
     * @param islandTileDestination IslandTile where MotherNature should end
     * @param moveCount Number of moves allowed
     * @param islandGroups List containing all the IslandGroups to check legal movement
     */
    @Override
    public void moveMotherNature(IslandTile islandTileStarting, IslandTile islandTileDestination, int moveCount, List<IslandGroup> islandGroups) throws IllegalArgumentException {
        int startingIslandGroupNumber = 0;
        int endingIslandGroupNumber = 0;
        int moveCountNeeded;
        for(IslandGroup islandGroup: islandGroups){
            if(islandGroup.equals(islandTileStarting.getIslandGroup()))
                startingIslandGroupNumber = islandGroups.indexOf(islandGroup);
            if(islandGroup.equals(islandTileDestination.getIslandGroup()))
                endingIslandGroupNumber = islandGroups.indexOf(islandGroup);
        }
        moveCountNeeded = moveCount(startingIslandGroupNumber + 1, endingIslandGroupNumber + 1, islandGroups.size());
        if(moveCountNeeded == 0 || moveCountNeeded > moveCount)
            throw new IllegalArgumentException("Move power is not enough to make this move!");
        else{
            MotherNature motherNature = islandTileStarting.removeMotherNature();
            islandTileDestination.placeMotherNature(motherNature);
        }
    }

    private int moveCount(int startingNumber, int endingNumber, int maxNumber){
        int temp = endingNumber - startingNumber;
        if(temp < 0)
            return maxNumber + temp;
        else
            return temp;
    }
}
