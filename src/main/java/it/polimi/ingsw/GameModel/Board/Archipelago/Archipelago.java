package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.StandardMotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.StandardResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that models all the islands on the table.
 * It contains a list of IslandGroup to model the merged IslandTiles.
 */
public class Archipelago {

    /**
     * List that contains all the IslandGroups. The order of the list is used to merge and render the islands
     */
    private List<IslandGroup> islandGroups;

    /**
     * Strategy to apply when resolving an island
     */
    private ResolveStrategy resolveStrategy;

    /**
     * Strategy to apply when moving MotherNature
     */
    private MotherNatureStrategy motherNatureStrategy;

    /**
     * Creates the Archipelago and creates 12 IslandGroups,
     */
    public Archipelago(){
        islandGroups = new ArrayList<IslandGroup>();
        int startingIsland = selectStartingIsland();
        for (int i = 0; i < 12; i++) {
            islandGroups.add(new IslandGroup(i==startingIsland));
        }
        motherNatureStrategy = new StandardMotherNatureStrategy();
        resolveStrategy = new StandardResolveStrategy();
    }

    /**
     * Selects the IslandGroup which holds the starting IslandTile
     * @return A random int between 0 and 11
     */
    private int selectStartingIsland(){
        Random random = new Random();
        return random.nextInt(12);
    }

    /**
     * Places students on the IslandTile at the start of the game. Places given students on each IslandTile skipping the one with MotherNature and its opposite
     * @param students 10 random students, 2 of each color
     */
    public void initialStudentPlacement(List<Student> students){
        int idxStudent = 0;
        IslandGroup islandGroup = getMotherNatureIslandGroup();
        int idxStarting = islandGroups.indexOf(islandGroup);
        for (int i = 1; i < 12; i++) {
            if(i != 6){
                int idx = idxStarting + i < 12 ? idxStarting + i : idxStarting + i - 12;
                islandGroups.get(idx).placeStudent(students.get(idxStudent));
                idxStudent++;
            }
        }
    }

    /**
     * Calls the motherNatureStrategy method to handle moving MotherNature
     * @param islandTileDestination Island group selected by the user
     * @param moveCount Allowed island that MotherNature can move
     */
    public void moveMotherNature(IslandTile islandTileDestination, int moveCount) throws InvalidObjectException {
        motherNatureStrategy.moveMotherNature(getMotherNatureIslandTile(), islandTileDestination, moveCount, islandGroups);
    }

    /**
     * Finds which IslandGroups contains  MotherNature
     * @return The IslandGroup containing MotherNature. Should never return null
     */
    private  IslandGroup getMotherNatureIslandGroup(){
        for(IslandGroup islandGroup : islandGroups){
            if(islandGroup.hasMotherNature())
                return islandGroup;
        }
        return  null;
    }
    /**
     * Finds which IslandGroups contains the IslandTile containing MotherNature
     * @return The IslandTile containing MotherNature. Should never return null
     */
    private IslandTile getMotherNatureIslandTile(){
        for(IslandGroup islandGroup : islandGroups){
            if(islandGroup.hasMotherNature())
                return islandGroup.getMotherNatureTile();
        }
        return null; //should never reach here
    }

    /**
     * Resolves IslandGroup
     * @param islandGroup IslandGroup to resolve
     * @param teams Teams in play who could get ownership of the islandGroup
     * @param professorSet The set to manage professor ownership and calculate influence
     */
    public void resolveIslandGroup(IslandGroup islandGroup, List<Team> teams, ProfessorSet professorSet){
        Team winner = resolveStrategy.resolveIslandGroup(islandGroup,teams, professorSet);
        boolean swapped = conquerIslandGroup(islandGroup, winner);
        if(swapped)
            mergeIslandGroup(islandGroup);
    }

    /**
     * Conquers the IslandGroup, replacing (if existing) towers with those of team
     * @param islandGroup The IslandGroup to conquer
     * @param team The team who conquers the IslandGroup
     */
    private boolean conquerIslandGroup(IslandGroup islandGroup, Team team){
        return islandGroup.conquer(team);
    }

    /**
     * Merges given IslandGroup with nearby IslandGroups
     * @param islandGroupToMerge The IslandGroup to merge
     */
    private void mergeIslandGroup(IslandGroup islandGroupToMerge){
        int idxToMerge = islandGroups.indexOf(islandGroupToMerge);
        int idxLeft = idxToMerge == 0 ?
                islandGroups.size() - 1 : idxToMerge - 1;
        int idxRight = idxToMerge == islandGroups.size() - 1 ?
                0 : idxToMerge + 1;
        TowerColor tcToMerge = islandGroupToMerge.getTowerColor();
        TowerColor tcLeft = islandGroups.get(idxLeft).getTowerColor();
        TowerColor tcRight = islandGroups.get(idxRight).getTowerColor();
        if(tcToMerge != tcLeft && tcToMerge != tcRight)
            return;
        else{
            if(tcToMerge == tcLeft) {
                islandGroupToMerge.addIslandTilesBefore(islandGroups.get(idxLeft).removeIslandTiles());
                islandGroups.remove(islandGroups.get(idxLeft));
            }
            if(tcToMerge == tcRight){
                islandGroupToMerge.addIslandTilesAfter(islandGroups.get(idxRight).removeIslandTiles());
                islandGroups.remove(islandGroups.get(idxRight));
            }
        }
        mergeIslandGroup(islandGroupToMerge); //FIXME: checks always right and left, even if useless to check left. Could divide in mergeLeft and mergeRight (probably not worth it)
    }

    /**
     * Places the Student in the given IslandTile
     * @param student Student to place
     * @param islandTile IslandTile where student must be placed
     */
    public void placeStudent(Student student, IslandTile islandTile){
        for(IslandGroup islandGroup : islandGroups){ //CHECKME: you could just call islandTile.placeStudent withouth checking anything, this just delegates the action to the islandgroup which actually contains the tile
            if(islandGroup.hasIslandTile(islandTile))
                islandGroup.placeStudent(student, islandTile);
        }
    }

    //TODO: add search by IDs

}
