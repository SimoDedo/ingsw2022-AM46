package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.*;
import it.polimi.ingsw.Utils.Enum.Color;

import java.util.List;

public class IslandTile extends StudentContainer{
    /**
     * Holds MotherNature if IslandTile is current holder, otherwise it's null
     */
    private MotherNature motherNature = null;

    /**
     * Holds tower currently placed on the IslandTile. It's null if no tower has been placed
     */
    private Tower tower = null;

    /**
     * Holds the IslandGroup of which the IslandTile is part
     */
    private IslandGroup islandGroup; //CHECKME: is it useless?

    /**
     * Creates IslandTile with owner and maxPawns. If it's a starting island, also instantiates MotherNature
     *
     * @param player   the player who owns the container
     * @param maxPawns the max number of pawns the container can hold
     * @param isStarting whether this IslandTile should instantiate (and hold) MotherNature
     */
    public IslandTile(Player player, int maxPawns, boolean isStarting, IslandGroup islandGroup) {
        super(player, maxPawns);
        this.islandGroup = islandGroup;
        if (isStarting)
            motherNature = new MotherNature(this);
    }

    /**
     * Places MotherNature on IslandTile
     * @param motherNature
     */
    public void placeMotherNature(MotherNature motherNature) {
        this.motherNature = motherNature;
    }

    /**
     * Removes MotherNature from IslandTile, thus leaving it null
     * @return
     */
    public MotherNature removeMotherNature() {
        MotherNature temp = motherNature;
        motherNature = null;
        return temp;
    }

    /**
     * Returns true if it holds MotherNature
     * @return
     */
    public boolean hasMotherNature(){
        return motherNature != null;
    }

    /**
     * Getter for the Tower
     * @return
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * Swaps and return current Tower with given tower. Returns null if no tower was placed beforehand
     * @param tower
     * @return
     */
    public Tower swapTower(Tower tower) {
        Tower temp = this.tower;
        this.tower = tower;
        this.setOwner(tower.getOwner());
        return temp;
    }

    /**
     * Getter for the IslandGroup
     * @return
     */
    public IslandGroup getIslandGroup() {
        return islandGroup;
    }

    /**
     * Setter for the IslandGroup
     * @param islandGroup
     */
    public void setIslandGroup(IslandGroup islandGroup) {
        this.islandGroup = islandGroup;
    }

    /**
     * Counts number of student with given Color
     * @param color
     * @return
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
