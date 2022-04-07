package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.*;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;

import java.util.List;

public class IslandTile extends StudentContainer{
    /**
     * Holds a reference to MotherNature if IslandTile is the current holder, otherwise it's null.
     */
    private MotherNature motherNature = null;

    /**
     * Holds a reference to the Tower currently placed on the IslandTile. It's null if no tower
     * is currently placed on the tile.
     */
    private Tower tower = null;

    /**
     * Holds the IslandGroup of which the IslandTile is part. Deprecated.
     */
    private IslandGroup islandGroup; // considering removing this variable in the future (it's useless as of now)

    /**
     * Constructor for IslandTile with an owner and a maximum number of students that it can hold.
     * If it's a starting island, the constructor also instantiates MotherNature.
     *
     * @param player the Player who owns the container
     * @param isStarting whether this IslandTile should instantiate (and hold) MotherNature
     * @param islandGroup the IslandGroup that owns the tile
     */
    public IslandTile(Player player, boolean isStarting, IslandGroup islandGroup) {
        super(player, 130);
        this.islandGroup = islandGroup;
        if (isStarting)
            motherNature = new MotherNature(this);
    }

    /**
     * Places MotherNature on this IslandTile.
     * @param motherNature MotherNature instance
     */
    public void placeMotherNature(MotherNature motherNature) {
        this.motherNature = motherNature;
        motherNature.setIslandTile(this);
    }

    /**
     * Removes MotherNature from IslandTile, thus setting the motherNature variable to null.
     * @return the MotherNature instance if present, null otherwise
     */
    public MotherNature removeMotherNature() {
        MotherNature temp = motherNature;
        motherNature = null;
        return temp;
    }

    /**
     * Returns true if this IslandTile holds MotherNature.
     * @return true if motherNature is not null, false otherwise
     */
    public boolean hasMotherNature(){
        return motherNature != null;
    }

    /**
     * Getter for the color of the Tower placed on this IslandTile.
     * @return color of the tile's tower if present, null otherwise
     */
    public TowerColor getTowerColor() {
        return tower == null ? null : tower.getColor();
    }

    /**
     * Getter for the ID of the Tower placed on this IslandTile.
     * @return ID of the tile's tower if present, null otherwise
     */
    public Integer getTowerID(){
        return tower == null ? null : tower.getID();
    }
    /**
     * Swaps and return current Tower with the given new Tower. Returns null if no Tower is present
     * on the island.
     * @param newTower
     * @return
     */
    public Tower swapTower(Tower newTower) {
        Tower temp = this.tower;
        this.tower = newTower;
        this.setOwner(newTower.getOwner());
        return temp;
    }

    /**
     * Getter for the IslandGroup which holds this IslandTile.
     * @return the IslandGroup that holds this tile
     */
    public IslandGroup getIslandGroup() {
        return islandGroup;
    }

    /**
     * Setter for the islandGroup variable.
     * @param islandGroup the future IslandGroup that will own this tile
     */
    public void setIslandGroup(IslandGroup islandGroup) {
        this.islandGroup = islandGroup;
    }

    /**
     * Method that counts the number of students with the given Color on this tile.
     * @param color the color of the students to count
     * @return the number of students with the given color
     */
    public int countInfluence(Color color){
        List<Student> contained = this.getPawns();
        int score = 0;
        for(Student student : contained){
            if(student.getColor() == color)
                score++;
        }
        return score;
    }

}
