package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private MoveMotherNatureStrategy moveMotherNatureStrategy;

    /**
     * Creates the Archipelago and creates 12 IslandGroups,
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
        moveMotherNatureStrategy.moveMotherNature(getMotherNatureIslandTile(), islandTileDestination, moveCount, islandGroups);
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
     * @param players players in play who could get ownership of the islandGroup
     * @param professorSet The set to manage professor ownership and calculate influence
     */
    public void resolveIslandGroup(IslandGroup islandGroup, PlayerList players, ProfessorSet professorSet) throws GameOverException{
        if(!islandGroup.isNoEntryTilePlaced()) {
            Player winner = resolveStrategy.resolveIslandGroup(islandGroup, players, professorSet);
            boolean swapped = conquerIslandGroup(islandGroup, winner);
            if (swapped)
                mergeIslandGroup(islandGroup);
        }
        else return; // consider refactoring this line
    }

    /**
     * Conquers the IslandGroup, replacing (if existing) towers with those of team
     * @param islandGroup The IslandGroup to conquer
     * @param player The tower holder of the team who conquers the IslandGroup
     */
    private boolean conquerIslandGroup(IslandGroup islandGroup, Player player) throws GameOverException {
        return islandGroup.conquer(player);
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
        for(IslandGroup islandGroup : islandGroups) { // CHECKME: you could just call islandTile.placeStudent withouth checking anything, this just delegates the action to the islandgroup which actually contains the tile
            if(islandGroup.hasIslandTile(islandTile))
                islandGroup.placeStudent(student, islandTile);
        }
    }

    /**
     * Setter for the strategy used to resolve an island
     * @param resolveStrategy The strategy to apply
     */
    public void setResolveStrategy(ResolveStrategy resolveStrategy) {
        this.resolveStrategy = resolveStrategy;
    }

    /**
     * Setter for the strategy used to move MotherNature
     * @param motherNatureStrategy The strategy to apply
     */
    public void setMotherNatureStrategy(MoveMotherNatureStrategy motherNatureStrategy) {
        this.moveMotherNatureStrategy = moveMotherNatureStrategy;
    }

    /**
     * Finds a Student with a given ID
     * @param ID The ID of the student
     * @return The Student
     * @throws NoSuchElementException When no IslandGroup contains Student with such ID
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
     * Finds a IslandTile with a given ID
     * @param ID The ID of the IslandTile
     * @return The IslandTile
     * @throws NoSuchElementException When no IslandGroup contains IslandTile with such ID
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

    public void moveStudent(Student student, IslandTile islandTile) {} //todo

    //TODO: add search by IDs and get by IDs (useful for view i think)

}
