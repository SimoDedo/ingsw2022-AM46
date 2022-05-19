package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.List;

/**
 * This subclass of AbstractCharacter sets the NoEntryTilesSpace on the islands that receive a
 * NoEntryTile. It stores an initial number of NoEntryTiles and has adder/remover methods.
 */
public class NoEntryCharacter extends AbstractCharacter {

    private List<RequestParameter> requestParameters;

    private int noEntryTiles;
    private int maxNoEntryTiles;

    public NoEntryCharacter(int ID, int cost, int maxNoEntryTiles, List<RequestParameter> requestParameters) {
        super(ID, cost, requestParameters);
        this.noEntryTiles = maxNoEntryTiles;
        this.maxNoEntryTiles = maxNoEntryTiles;
    }

    /**
     * Along with the standard character activation, it checks that at least one no entry tile is left on the
     * @param owner the player who activated this character
     * @return a list of RequestParameters that will be needed by the game controller
     */
    @Override
    public List<RequestParameter> useCharacter(Player owner) throws IllegalStateException{
        if(noEntryTiles == 0)
            throw  new IllegalStateException("There are no more no entry tiles left!");
        return super.useCharacter(owner);
    }

    /**
     * Getter for the number of NoEntryTiles.
     * @return the number of NoEntryTiles on this character
     */
    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    /**
     * Adds one NoEntryTile to this character (unless it already has the maximum number of tiles).
     */
    public void addNoEntryTile() {
        if (noEntryTiles == maxNoEntryTiles) throw new IllegalStateException("Character already has the maximum number of no entry tiles");
        noEntryTiles++;
    }

    /**
     * Removes one NoEntryTile from this character (unless it doesn't have any).
     */
    public void removeNoEntryTile() {
        if (noEntryTiles == 0) throw new IllegalStateException("Character doesn't have any no entry tiles");
        noEntryTiles--;
    }

}
