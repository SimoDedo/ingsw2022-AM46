package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.*;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.GameModel.Characters.NoEntryTileCharacter;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Model a group of island tiles
 */
public class IslandGroup {
    /**
     * IslandTiles that compose the IslandGroup
     */
    private List<IslandTile> islandTiles;

    /**
     * Model NoEntryTiles present on given IslandGroup
     */
    private int noEntryTiles = 0;

    public IslandGroup(boolean isStarting){
        this.islandTiles = new ArrayList<>();
        this.islandTiles.add(new IslandTile(null, isStarting, this)); //CHECKME: player null replace with "neutral"?
    }

    public boolean hasMotherNature(){
        for(IslandTile islandTile : islandTiles){
            if(islandTile.hasMotherNature())
                return true;
        }
        return false;
    }
    /**
     * Returns the IslandTile which contains MotherNature
     * @return
     */
    public IslandTile getMotherNatureTile(){
        IslandTile temp = null;
        for(IslandTile islandTile : islandTiles){
            if(islandTile.hasMotherNature())
                temp = islandTile;
        }
        return temp; //CHECKME: consider throwing exception; right now whoever calls this method needs to check before if islandGroup has MotherNature
    }

    /**
     * Counts the influence of a color in the whole island group, checking each IslandTile
     * @param color The color of which we compute the influence
     * @return The influence of that color
     */
    public int countInfluence(Color color){
        int score = 0;
        for(IslandTile islandTile : islandTiles){
            score += islandTile.countInfluence(color);
        }
        return score;
    }

    /**
     * If the Towers on the IslandTiles aren't of the given Team, replaces the Towers with theirs
     * @param player The team who owns the towers on the IslandTile, once Conquer has finished
     * @return Returns true if towers had to be swapped, false if not
     */
    public boolean conquer(Player player) throws GameOverException {
        if(player != null && !player.getTowerColor().equals(this.getTowerColor())){
            for(IslandTile islandTile : islandTiles){
                Tower towerToPut = player.takeTower();
                Tower towerRemoved = islandTile.swapTower(towerToPut);
                if(towerRemoved != null) {
                    towerRemoved.getOwner().placeTower(towerRemoved);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Empties the islandTiles list
     * @return A copy of the islandTiles list before it was emptied, where each IslandTile has IslandGroup set to null
     */
    public List<IslandTile> removeIslandTiles(){
        ArrayList<IslandTile> temp = new ArrayList<IslandTile>();
        temp.addAll(islandTiles);
        islandTiles.removeAll(islandTiles);
        for(IslandTile islandTile : temp)
            islandTile.setIslandGroup(null);
        return temp;
    }

    public void selfDestruct(){
        //??????????????????????????????????????????
    }

    /**
     * Adds the given IslandTiles before those in IslandTile, keeping their order
     * @param islandTilesToAdd IslandTiles to be added
     */
    public void addIslandTilesBefore(List<IslandTile> islandTilesToAdd){
        for(IslandTile islandTile : islandTilesToAdd){
            islandTile.setIslandGroup(this);
        }
        islandTiles.addAll(0, islandTilesToAdd);
    }

    /**
     * Adds the given IslandTiles after those in IslandTile, keeping their order
     * @param islandTilesToAdd IslandTiles to be added
     */
    public void addIslandTilesAfter(List<IslandTile> islandTilesToAdd){
        for(IslandTile islandTile : islandTilesToAdd){
            islandTile.setIslandGroup(this);
        }
        islandTiles.addAll(islandTilesToAdd);
    }

    /**
     * Return true if IslandGroup contains the given IslandTile
     * @param islandTileToFind
     * @return
     */
    public boolean hasIslandTile(IslandTile islandTileToFind){
        for(IslandTile  islandTile : islandTiles){
            if(islandTile.getID() == islandTileToFind.getID())
                return true;
        }
        return false;
    }

    /**
     * Places the student in the tile, using the method of StudentContainer. Checks if the group owns the tile, otherwise exception
     * @param student The student to place
     * @param islandTile The tile where it must be placed
     */
    public void placeStudent(Student student, IslandTile islandTile){
        if(hasIslandTile(islandTile)) //this should be true, already checked in Archipelago.placeStudent
            islandTile.moveStudent(student);
    }

    /**
     * Places the student in the first tile, using the method of StudentContainer. Puts it in the first IslandTile
     * @param student The student to place
     */
    public void placeStudent(Student student){
            islandTiles.get(0).moveStudent(student);
    }

    /**
     * Returns the Color of the towers on the IslandGroup.
     * @return Color of the tower
     */
    public TowerColor getTowerColor(){
        if(islandTiles.get(0).getTowerColor() == null)
            return null;
        else
            return islandTiles.get(0).getTowerColor();
    }

    /**
     * Returns teh number of towers
     * @return Either 0 (first island has no tower, so there are 0 towers) or equal to number of IslandTiles (first island has a tower, so all do)
     */
    public int getTowerCount(){
        if(islandTiles.get(0).getTowerColor() == null)
            return 0;
        else
            return islandTiles.size();
    }

    public List<IslandTile> getIslandTiles() { return islandTiles; }

    /**
     * Adds a NoEntryTile to this IslandGroup
     */
    public void addNoEntryTile(){
        noEntryTiles++;
    }

    /**
     * Checks if there is a NoEntryTile placed. If there is, it removes one
     * @return True if there is at least one NoEntryTile
     */
    public boolean isNoEntryTilePlaced(){
        if(noEntryTiles>0){
            noEntryTiles--;
            NoEntryTileCharacter.putBackNoEntryTile();
            return true;
        }
        else
            return false;
    }

    /**
     * Finds a Student with a given ID
     * @param ID The ID of the student
     * @return The Student if found, null otherwise
     */
    public Student getStudentByID(int ID){
        Student studentToReturn = null;
        for (IslandTile islandTile : islandTiles){
            try{
                studentToReturn =  islandTile.getPawnByID(ID);
            }
            catch (NoSuchElementException e){
            }
        }
        if(studentToReturn != null)
            return studentToReturn;
        else
            throw new NoSuchElementException("No such student in this IslandGroup");
    }

    /**
     * Collects all IDs of students in IslandTiles
     * @return A list of the Students IDs
     */
    public List<Integer> getStudentIDs(){
        List<Integer> studentsIDs = new ArrayList<Integer>();
        for (IslandTile islandTile : islandTiles){
            studentsIDs.addAll(islandTile.getPawnIDs());
        }
        return  studentsIDs;
    }

    /**
     * Finds a IslandTile with a given ID
     * @param ID The ID of the IslandTile
     * @return The IslandTile if found, null otherwise
     */
    public IslandTile getIslandTileByID(int ID){
        for (IslandTile islandTile : islandTiles){
            if(islandTile.getID() == ID)
                return islandTile;
        }
        return null;
    }

    /**
     * Gets all the IslandTile IDs contained in IslandGroup
     * @return List with all IslandTiles ID
     */
    public  List<Integer> getIslandTileIDs(){
        List<Integer> islandTiledIDs = new ArrayList<>();
        for(IslandTile islandTile : islandTiles){
            islandTiledIDs.add(islandTile.getID());
        }
        return islandTiledIDs;
    }


}
