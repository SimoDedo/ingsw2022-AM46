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
 * Class that models a group of IslandTiles. When created, a Group only contains one Tile;
 * however, the number of Tiles can increase by merging Groups.
 */
public class IslandGroup {
    /**
     * List of IslandTiles that compose the IslandGroup.
     */
    private List<IslandTile> islandTiles;

    /**
     * Integer that models the number of NoEntryTiles present on this IslandGroup.
     */
    private int noEntryTiles = 0;

    /**
     * Constructor for IslandGroup. It creates one IslandTiles and passes it the isStarting boolean.
     * @param isStarting
     */
    public IslandGroup(boolean isStarting){
        this.islandTiles = new ArrayList<IslandTile>();
        this.islandTiles.add(new IslandTile(null, isStarting, this)); //CHECKME: player null replace with "neutral"?
    }

    /**
     * Boolean method that returns true if one of the tiles in this IslandGroup contains MotherNature.
     * @return
     */
    public boolean hasMotherNature(){
        for(IslandTile islandTile : islandTiles){
            if(islandTile.hasMotherNature())
                return true;
        }
        return false;
    }
    /**
     * Returns the IslandTile which contains MotherNature.
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
     * Counts the influence of a given color in the whole IslandGroup, checking each IslandTile.
     * @param color the color of which to calculate the influence
     * @return the influence of the given color
     */
    public int countInfluence(Color color){
        int score = 0;
        for(IslandTile islandTile : islandTiles){
            score += islandTile.countInfluence(color);
        }
        return score;
    }

    /**
     * Method that checks if the Towers on the IslandTiles don't belong to the given Team,
     * and have to be replaced with the ones belonging to it. It then swaps the Towers.
     * @param team the team who conquered the IslandGroup
     * @return true if the towers on the islands have to be replaced, false if not
     */
    public boolean conquer(Team team) throws GameOverException {
        if(team != null && !team.getColor().equals(this.getTowerColor())){
            for(IslandTile islandTile : islandTiles){
                Tower towerToPut = team.getPlayerWithTowers().takeTower();
                if(towerToPut == null){
                    //CHECKME: se finite le torri, vinta partita => andrà qui il notify o in towerspace? nel dubbio lascio questo commento piangetene a rigaurdo
                }
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
     * Empties the islandTiles list, returning it.
     * @return a copy of the islandTiles list before it was emptied, where each IslandTile has IslandGroup set to null
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
     * Method that adds the given IslandTiles before those already present in the islandTile list,
     * keeping their internal order.
     * @param islandTilesToAdd IslandTiles to be added
     */
    public void addIslandTilesBefore(List<IslandTile> islandTilesToAdd){
        for(IslandTile islandTile : islandTilesToAdd){
            islandTile.setIslandGroup(this);
        }
        islandTiles.addAll(0, islandTilesToAdd);
    }

    /**
     * Method that adds the given IslandTiles after those already present in the islandTile list,
     * keeping their internal order.
     * @param islandTilesToAdd IslandTiles to be added
     */
    public void addIslandTilesAfter(List<IslandTile> islandTilesToAdd){
        for(IslandTile islandTile : islandTilesToAdd){
            islandTile.setIslandGroup(this);
        }
        islandTiles.addAll(islandTilesToAdd);
    }

    /**
     * Method that returns true if IslandGroup contains the given IslandTile
     * @param islandTileToFind the islandTile to find, duh
     * @return true if the island is inside this IslandGroup, false otherwise
     */
    public boolean hasIslandTile(IslandTile islandTileToFind){
        for(IslandTile  islandTile : islandTiles){
            if(islandTile.getID() == islandTileToFind.getID())
                return true;
        }
        return false;
    }

    /**
     * This method places the student in the Tile, calling the moveStudent method in StudentContainer.
     * It checks whether the group owns the Tile, and does nothing if not.
     * @param student The student to place
     * @param islandTile The tile where it must be placed
     */
    public void placeStudent(Student student, IslandTile islandTile) {
        if(hasIslandTile(islandTile)) // this should be true, already checked in Archipelago.placeStudent
            islandTile.moveStudent(student);
        // consider throwing an exception in the else branch
    }

    /**
     * Places the student in the first tile, calling the moveStudent method in StudentContainer.
     * @param student the student to be placed
     */
    public void placeStudent(Student student){
            islandTiles.get(0).moveStudent(student);
    }

    /**
     * Returns the Color of the towers on the IslandGroup. Specifically, it only checks the first tile.
     * @return color of the Towers in the group
     */
    public TowerColor getTowerColor(){
        if(islandTiles.get(0).getTowerColor() == null)
            return null;
        else
            return islandTiles.get(0).getTowerColor();
    }

    /**
     * Getter for the total number of Towers on this IslandGroup.
     * @return either 0 (first island has no tower, so there are 0 towers) or the number of
     * IslandTiles in this group (first island has a tower, so all do)
     */
    public int getTowerCount(){
        if(islandTiles.get(0).getTowerColor() == null)
            return 0;
        else
            return islandTiles.size();
    }

    /**
     * Getter for the list of IslandTiles inside this group.
     * @return a list containing all IslandTiles inside this IslandGroup
     */
    public List<IslandTile> getIslandTiles() { return islandTiles; }

    /**
     * Method that adds a NoEntryTile to this IslandGroup.
     */
    public void addNoEntryTile(){
        noEntryTiles++;
    }

    /**
     * Checks if there is a NoEntryTile placed. If there is, it removes one and returns true.
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
     * Finds a Student with the given ID.
     * @param ID the ID of the student to find
     * @return the Student if found, null otherwise
     */
    public Student findStudentByID(int ID){
        Student studentToReturn = null;
        for (IslandTile islandTile : islandTiles){
            try{
                studentToReturn =  islandTile.getPawnByID(ID);
            }
            catch (NoSuchElementException ignored){
            }
        }
        if(studentToReturn != null)
            return studentToReturn;
        else
            throw new NoSuchElementException("No such student in this IslandGroup");
    }

    /**
     * Finds an IslandTile with the given ID.
     * @param ID the ID of the IslandTile to find
     * @return the IslandTile if found, null otherwise
     */
    public IslandTile findIslandTileByID(int ID){
        for (IslandTile islandTile : islandTiles){
            if(islandTile.getID() == ID)
                return islandTile;
        }
        return null;
    }


}
