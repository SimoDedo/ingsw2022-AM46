package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.*;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.Tower;
import it.polimi.ingsw.Utils.Enum.Color;
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
        this.islandTiles = new ArrayList<IslandTile>();
        this.islandTiles.add(new IslandTile(null, -1, isStarting, this)); //CHECKME: player null replace with "neutral"?
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
     * @param team The team who owns the towers on the IslandTile, once Conquer has finished
     * @return Returns true if towers had to be swapped, false if not
     */
    public boolean conquer(Team team){
        if(team != null && team.getColor() != this.getTowerColor()){
            for(IslandTile islandTile : islandTiles){
                Tower towerToPut = team.getPlayerWithTowers().takeTower();
                if(towerToPut == null){
                    //CHECKME: se fintie le torri, vinta partita => andrà qui il notify o in towerspace? nel dubbio lascio questo commento piangetene a rigaurdo
                }
                Tower towerRemoved = islandTile.swapTower(towerToPut);
                if(towerRemoved != null)
                    towerRemoved.getOwner().putTower(towerRemoved);
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
        if(islandTiles.get(0).getTower() == null)
            return null;
        else
            return islandTiles.get(0).getTower().getColor();
    }

    /**
     * Returns teh number of towers
     * @return Either 0 (first island has no tower, so there are 0 towers) or equal to number of IslandTiles (first island has a tower, so all do)
     */
    public int getTowerCount(){
        if(islandTiles.get(0).getTower() == null)
            return 0;
        else
            return islandTiles.size();
    }
}
