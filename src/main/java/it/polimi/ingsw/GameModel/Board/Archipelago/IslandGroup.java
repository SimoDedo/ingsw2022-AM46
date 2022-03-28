package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.*;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Model a group of island tiles
 */
public class IslandGroup {
    /**
     * IslandTiles that compose the IslandGroup
     */
    private List<IslandTile> islandTiles;

    //TODO: int for NoEntryTiles

    public IslandGroup(boolean isStarting){
        islandTiles.add(new IslandTile(null, -1, isStarting, this)); //TODO: player null replace with "neutral"?
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
        return temp; //TODO: consider throwing exception; right now whoever calls this method needs to check before if islandGroup has MotherNature
    }

    /**
     * Computes the Team which holds the most influence over the IslandGroup
     * @param teams List to iterate to compute which team holds more influence
     * @param professorSet Needed to know which team holds the professor
     * @return The winning team, which will hold the towers of the tiles
     */
    public Team resolveWinner(List<Team> teams, ProfessorSet professorSet){
        return null;
    }

    /**
     * If the Towers on the IslandTiles aren't of the given Team, replaces the Towers with theirs
     * @param team The team who owns the towers on the IslandTile, once Conquer has finished
     */
    public void conquer(Team team){
        //TODO: Implement when player gets pushed/create stuff to implement
    }

    /**
     * Empties the islandTiles list
     * @return A copy of the islandTiles list before it was emptied
     */
    public List<IslandTile> removeIslandTiles(){
        ArrayList<IslandTile> temp = new ArrayList<IslandTile>();
        temp.addAll(islandTiles);
        islandTiles.removeAll(islandTiles);
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
            islandTiles.addAll(0, islandTilesToAdd);
    }

    /**
     * Adds the given IslandTiles after those in IslandTile, keeping their order
     * @param islandTilesToAdd IslandTiles to be added
     */
    public void addIslandTilesAfter(List<IslandTile> islandTilesToAdd){
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
            islandTile.placePawn(student); //FIXME: we will probably implement moveStudent, this will change
    }

    /**
     * Returns the Color of the towers on the IslandGroup.
     * @return
     */
    public TowerColor getTowerColor(){
        return islandTiles.get(0).getTower().getTowerColor();
    }
}
