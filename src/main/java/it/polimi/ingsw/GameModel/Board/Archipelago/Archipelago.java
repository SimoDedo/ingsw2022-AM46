package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategy;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyStandard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;

import java.io.InvalidObjectException;
import java.util.*;

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

    //region Creation
    /**
     * Creates the Archipelago and creates 12 IslandGroups,
     */
    public Archipelago() {
        islandGroups = new ArrayList<>();
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
    //endregion

    //region MotherNature management

    /**
     * Calls the motherNatureStrategy method to handle moving MotherNature
     * @param islandTileDestination Island group selected by the user
     * @param moveCount Allowed island that MotherNature can move
     */
    public void moveMotherNature(IslandTile islandTileDestination, int moveCount) throws InvalidObjectException {
        moveMotherNatureStrategy.moveMotherNature(getMotherNatureIslandTile(), islandTileDestination, moveCount, islandGroups);
    }

    /**
     * Calls the motherNatureStrategy method to handle moving MotherNature
     * @param islandTileDestinationID Island group selected by the user
     * @param moveCount Allowed island that MotherNature can move
     */
    public void moveMotherNature(int islandTileDestinationID, int moveCount) throws InvalidObjectException {
        moveMotherNatureStrategy.moveMotherNature(getMotherNatureIslandTile(), getIslandTileByID(islandTileDestinationID), moveCount, islandGroups);
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
    //endregion

    //region Resolve methods

    /**
     * Resolves IslandGroup
     * @param islandGroup IslandGroup to resolve
     * @param players players in play who could get ownership of the islandGroup
     * @param professorSet The set to manage professor ownership and calculate influence
     */
    public void resolveIslandGroup(IslandGroup islandGroup, PlayerList players, ProfessorSet professorSet)
            throws GameOverException{
        if(!islandGroup.isNoEntryTilePlaced()) {
            Player winner = resolveStrategy.resolveIslandGroup(islandGroup, players, professorSet);
            boolean swapped = conquerIslandGroup(islandGroup, winner);
            if (swapped)
                mergeIslandGroup(islandGroup);
        }
        else return; // consider refactoring this line (on my corpse) i want 2 cry
    }

    /**
     * Resolves IslandGroup: ID variant
     * @param islandGroupIndex IslandGroupID to resolve
     * @param players players in play who could get ownership of the islandGroup
     * @param professorSet The set to manage professor ownership and calculate influence
     */
    public void resolveIslandGroup(int islandGroupIndex, PlayerList players, ProfessorSet professorSet)
            throws GameOverException{
        if(!islandGroups.get(islandGroupIndex).isNoEntryTilePlaced()) {
            Player winner = resolveStrategy.resolveIslandGroup(islandGroups.get(islandGroupIndex), players, professorSet);
            boolean swapped = conquerIslandGroup(islandGroups.get(islandGroupIndex), winner);
            if (swapped)
                mergeIslandGroup(islandGroups.get(islandGroupIndex));
        }
        else return; // consider refactoring this line (on my corpse)
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
    private void mergeIslandGroup(IslandGroup islandGroupToMerge) throws GameOverException{
        if(getNumOfIslandGroups() == 4){ throw new GameOverException(); }
        int idxToMerge = islandGroups.indexOf(islandGroupToMerge);
        TowerColor tcToMerge = islandGroupToMerge.getTowerColor();

        int idxLeft = idxToMerge == 0 ?
                islandGroups.size() - 1 : idxToMerge - 1;
        TowerColor tcLeft = islandGroups.get(idxLeft).getTowerColor();

        if(tcToMerge == tcLeft) {
            islandGroupToMerge.addIslandTilesBefore(islandGroups.get(idxLeft).removeIslandTiles());
            islandGroups.remove(islandGroups.get(idxLeft));
        }

        idxToMerge = islandGroups.indexOf(islandGroupToMerge);
        int idxRight = idxToMerge == islandGroups.size() - 1 ?
                0 : idxToMerge + 1;
        TowerColor tcRight = islandGroups.get(idxRight).getTowerColor();
        if(tcToMerge == tcRight){
            islandGroupToMerge.addIslandTilesAfter(islandGroups.get(idxRight).removeIslandTiles());
            islandGroups.remove(islandGroups.get(idxRight));

        }
        if(tcToMerge != tcLeft && tcToMerge != tcRight)
            return;
        else
            mergeIslandGroup(islandGroupToMerge);
                                                //FIXME: checks always right and left, even if useless to check left.
                                                // - Could divide in mergeLeft and mergeRight (probably not worth it)
                                                // -  probably no need recursion (if two of the same tower are adjacent they should be merged)
    }

    //endregion

    //region Place Student

    /**
     * Places the Student in the given IslandTile
     * @param student Student to place
     * @param islandTile IslandTile where student must be placed
     */
    public void placeStudent(Student student, IslandTile islandTile){
        for(IslandGroup islandGroup : islandGroups) { // CHECKME: you could just call islandTile.placeStudent without checking anything, this just delegates the action to the islandgroup which actually contains the tile
            if(islandGroup.hasIslandTile(islandTile))
                islandGroup.placeStudent(student, islandTile);
        }
    }

    /**
     * Places the Student in the given IslandTile
     * @param student Student to place
     * @param islandTileID IslandTile where student must be placed
     */
    public void placeStudent(Student student, int islandTileID){
        for(IslandGroup islandGroup : islandGroups) { // CHECKME: you could just call islandTile.placeStudent without checking anything, this just delegates the action to the islandgroup which actually contains the tile
            if(islandGroup.hasIslandTile(getIslandTileByID(islandTileID)))
                islandGroup.placeStudent(student, getIslandTileByID(islandTileID));
        }
    }
    //endregion

    //region Strategy setters

    /**
     * Setter for the strategy used to resolve an island
     * @param resolveStrategy The strategy to apply
     */
    public void setResolveStrategy(ResolveStrategy resolveStrategy) {
        this.resolveStrategy = resolveStrategy;
    }

    /**
     * Setter for the strategy used to move MotherNature
     * @param moveMotherNatureStrategy The strategy to apply
     */
    public void setMotherNatureStrategy(MoveMotherNatureStrategy moveMotherNatureStrategy) {
        this.moveMotherNatureStrategy = moveMotherNatureStrategy;
    }
    //endregion

    //region State observer methods using IDs
    /**
     * Finds a Student with a given ID
     * @param ID The ID of the student
     * @return The Student
     * @throws NoSuchElementException When no IslandGroup contains Student with such ID
     */
    public Student getStudentByID(int ID) throws NoSuchElementException {
        Student studentToReturn = null;
        for (IslandGroup islandGroup : islandGroups) {
            try {
                studentToReturn = islandGroup.getStudentByID(ID);
            } catch (NoSuchElementException e) {
            }
        }
        if(studentToReturn == null)
            throw new NoSuchElementException("No Student with such ID in IslandGroups");
        else
            return studentToReturn;
    }

    /**
     * Searches all IslandGroup to find which student each contains
     * @return A HashMap containing as Key the idx of the IslandGroup, as value the list of StudentIDs
     */
    public HashMap<Integer, List<Integer>> getStudentsIDs(){
        HashMap<Integer, List<Integer>> studentIDs = new LinkedHashMap<>();
        for(IslandGroup islandGroup : islandGroups){
            studentIDs.put(islandGroups.indexOf(islandGroup),islandGroup.getStudentIDs());
        }
        return studentIDs;
    }

    /**
     * Returns the (current) index of the IslandGroup containing MotherNature
     * @return The (current) index of the IslandGroup containing MotherNature
     */
    public int getMotherNatureIslandGroupIndex(){
        for(IslandGroup islandGroup : islandGroups){
            if(islandGroup.hasMotherNature())
                return islandGroups.indexOf(islandGroup);
        }
        return -1;
    }

    public int getNumOfIslandGroups(){
        return islandGroups.size();
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
            IslandTile temp = islandGroup.getIslandTileByID(ID);
            if(temp != null)
                islandTileToReturn = temp;
        }
        if(islandTileToReturn == null)
            throw  new NoSuchElementException("No IslandTile with such ID in IslandGroups");
        else
            return islandTileToReturn;
    }

    /**
     * For each IslandGroup finds the IDs of its IslandTiles
     * @return An HashMap with key the (current) index of the IslandGroup and a list of its IslandTiles IDs
     */
    public HashMap<Integer, List<Integer>> getIslandTilesIDs(){
        HashMap<Integer, List<Integer>> islandTilesIDs = new LinkedHashMap<>();
        for(IslandGroup islandGroup : islandGroups){
            islandTilesIDs.put(islandGroups.indexOf(islandGroup), islandGroup.getIslandTileIDs());
        }
        return  islandTilesIDs;
    }

    public TowerColor getTowerColorOfIslandGroup(int islandGroupIndex){
        return islandGroups.get(islandGroupIndex).getTowerColor();
    }
    //endregion

}
