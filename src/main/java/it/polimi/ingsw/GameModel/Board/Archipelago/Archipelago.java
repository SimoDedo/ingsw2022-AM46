package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Class that models all the islands on the table. It contains a list of IslandGroups, which in
 * turn contain various IslandTiles. This class manages the merging of IslandGroups, finding
 * IslandTiles when a player decides to place a student on an island, and more.
 */
public class Archipelago {

    /**
     * List that contains all the IslandGroups. The order of the list is used to merge and render
     * the islands.
     */
    private List<IslandGroup> islandGroups;

    /**
     * Strategy to apply when resolving an island.
     */
    private ResolveStrategy resolveStrategy;

    /**
     * Strategy to apply when moving MotherNature.
     */
    private MoveMotherNatureStrategy moveMotherNatureStrategy;

    /**
     * Constructor for Archipelago. It creates 12 IslandGroups and sets moveMotherNature and
     * resolveIslandGroup strategies to their default version.
     */
    public Archipelago() {
        islandGroups = new ArrayList<IslandGroup>();
        int startingIsland = selectStartingIsland();
        for (int i = 0; i < 12; i++) {
            islandGroups.add(new IslandGroup(i==startingIsland));
        }
        moveMotherNatureStrategy = new MoveMotherNatureStrategyStandard();
        resolveStrategy = new ResolveStrategyStandard();
    }

    /**
     * Selects the IslandGroup which holds the starting IslandTile, i.e. the one that will first
     * house Mother Nature.
     * @return A random int between 0 and 11
     */
    private int selectStartingIsland(){
        Random random = new Random();
        return random.nextInt(12);
    }

    /**
     * Places students on the IslandTile at the start of the game. The method places the given
     * students on each IslandTile, skipping the one with MotherNature and its opposite.
     * @param students 10 random students, 2 of each color
     */
    public void initialStudentPlacement(List<Student> students){
        int idxStudent = 0;
        int idxStarting = islandGroups.indexOf(getMotherNatureIslandGroup());
        for (int i = 1; i < 12; i++) {
            if(i != 6){
                int idx = idxStarting + i < 12 ? idxStarting + i : idxStarting + i - 12;
                islandGroups.get(idx).placeStudent(students.get(idxStudent));
                idxStudent++;
            }
        }
    }

    /**
     * Calls the motherNatureStrategy method to handle moving MotherNature.
     * @param islandTileDestination Island group selected by the user, where they want MotherNature to land
     * @param moveCount Maximum number of hops that MotherNature can make between IslandGroups
     */
    public void moveMotherNature(IslandTile islandTileDestination, int moveCount) throws InvalidObjectException {
        moveMotherNatureStrategy.moveMotherNature(getMotherNatureIslandTile(), islandTileDestination, moveCount, islandGroups);
    }

    /**
     * Getter method that finds which IslandGroup contains MotherNature.
     * @return The IslandGroup containing MotherNature. Return value is never null
     */
    private IslandGroup getMotherNatureIslandGroup(){
        for(IslandGroup islandGroup : islandGroups){
            if(islandGroup.hasMotherNature())
                return islandGroup;
        }
        return null; // this statement should never be reached
    }

    /**
     * Getter method that finds which IslandGroup contains the IslandTile that in turn contains
     * MotherNature.
     * @return The IslandTile containing MotherNature. Return value is never null
     */
    private IslandTile getMotherNatureIslandTile(){
        for(IslandGroup islandGroup : islandGroups){
            if(islandGroup.hasMotherNature())
                return islandGroup.getMotherNatureTile();
        }
        return null; // this statement should never be reached
    }

    /**
     * Method for resolving an IslandGroup. It calls the current ResolveStrategy set in Archipelago.
     * @param islandGroup IslandGroup to resolve
     * @param teams Teams inside this match, that could get ownership of the islandGroup
     * @param professorSet class that manages each professor's ownership, used to calculate their influence
     */
    public void resolveIslandGroup(IslandGroup islandGroup, List<Team> teams, ProfessorSet professorSet) throws GameOverException{
        if(!islandGroup.isNoEntryTilePlaced()) {
            Team winner = resolveStrategy.resolveIslandGroup(islandGroup, teams, professorSet);
            boolean swapped = conquerIslandGroup(islandGroup, winner);
            if (swapped)
                mergeIslandGroup(islandGroup);
        }
        else return; // consider refactoring this line
    }

    /**
     * Conquers the IslandGroup, replacing the existing Tower with that of the winning team's
     * (unless the group was already owned by that team).
     * @param islandGroup The conquered IslandGroup
     * @param team The team who has just conquered the IslandGroup
     */
    private boolean conquerIslandGroup(IslandGroup islandGroup, Team team) throws GameOverException {
        return islandGroup.conquer(team);
    }

    /**
     * Merges the given IslandGroup with its nearby IslandGroups iteratively.
     * @param islandGroupToMerge The IslandGroup to merge
     */
    private void mergeIslandGroup(IslandGroup islandGroupToMerge){
        int idxToMerge = islandGroups.indexOf(islandGroupToMerge);
        int idxLeft = idxToMerge == 0 ? islandGroups.size() - 1
                : idxToMerge - 1;
        int idxRight = idxToMerge == islandGroups.size() - 1 ? 0
                : idxToMerge + 1;
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
     * @param islandTile IslandTile where the Student should be placed
     */
    public void placeStudent(Student student, IslandTile islandTile){
        for(IslandGroup islandGroup : islandGroups) { // CHECKME: you could just call islandTile.placeStudent without checking anything, this just delegates the action to the islandgroup which actually contains the tile
            if(islandGroup.hasIslandTile(islandTile))
                islandGroup.placeStudent(student, islandTile);
        }
    }

    /**
     * Setter for the resolveIslandGroup strategy.
     * @param resolveStrategy the strategy to set to Archipelago
     */
    public void setResolveStrategy(ResolveStrategy resolveStrategy) {
        this.resolveStrategy = resolveStrategy;
    }

    /**
     * Setter for the moveMotherNature strategy.
     * @param motherNatureStrategy the strategy to set to Archipelago
     */
    public void setMotherNatureStrategy(MoveMotherNatureStrategy motherNatureStrategy) {
        this.moveMotherNatureStrategy = moveMotherNatureStrategy;
    }

    /**
     * Finds the student with the given ID.
     * @param ID the ID of the student
     * @return the student, when found
     * @throws NoSuchElementException when no IslandGroup contains a student with such ID
     */
    public Student findStudentByID(int ID) throws NoSuchElementException {
        Student studentToReturn = null;
        for (IslandGroup islandGroup : islandGroups) {
            try {
                studentToReturn = islandGroup.findStudentByID(ID);
            } catch (NoSuchElementException e) {
            }
        }
        if(studentToReturn == null)
            throw new NoSuchElementException("No Student with such ID in IslandGroups");
        else
            return studentToReturn;
    }

    /**
     * Finds the IslandTile with the given ID.
     * @param ID the ID of the IslandTile
     * @return the IslandTile, when found
     * @throws NoSuchElementException when no IslandGroup contains an IslandTile with such ID
     */
    public IslandTile getIslandTileByID(int ID) throws NoSuchElementException{
        IslandTile islandTileToReturn = null;
        for (IslandGroup islandGroup : islandGroups){
            IslandTile temp = islandGroup.findIslandTileByID(ID);
            if(temp != null)
                islandTileToReturn = temp;
        }
        if(islandTileToReturn == null)
            throw  new NoSuchElementException("No IslandTile with such ID in IslandGroups");
        else
            return islandTileToReturn;
    }

    /**
     * Method that moves a Student from their original container to the given islandTile.
     * @param student the Student to be moved
     * @param islandTile the IslandTile where the Student should be placed
     */
    public void moveStudent(Student student, IslandTile islandTile) {} //todo

    //TODO: add search by IDs and get by IDs (useful for view i think)

}
