package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.*;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

import java.util.HashMap;
import java.util.List;

/**
 * Models character number 1,7,10,11,12
 */
public class MoveCharacter extends StudentContainer implements Character {

    /**
     * ID of the Character
     */
    private int ID = 0;

    /**
     * The player who paid for the Character
     */
    private Player playerUsing = null;

    /**
     * The cost of the Character
     */
    private int cost = 0;

    /**
     * True when someone paid for the Character during this turn, false otherwise
     */
    private boolean usedThisTurn;

    /**
     * True if no one has paid for this Character yet
     */
    private boolean isFirstUse;

    /**
     * The maximum number of time the Character Ability can be used in one turn
     */
    private int maxUses = 0;

    /**
     * The number of times the Character Ability has been used this turn
     */
    private int uses = 0;

    /**
     * List of the parameter the Character requests in order to apply its Ability
     */
    private List<RequestParameters> requestParameters;

    /**
     * The (only) bag of the game. Given only to Character that needs it
     */
    Bag bag = null;

    /**
     * The teams of the game. Given only to Character that needs it
     */
    List<Team> teams = null;

    /**
     * Constructor for the character
     * @param player Player who owns the Character card. Is always null/neutral player
     * @param maxPawns Maximum number of students that the card can contain (0 if no student has to be placed on the card)
     * @param ID ID of the Character
     * @param cost Cost of the Character
     * @param maxUses Maximum number of uses in a turn
     * @param requestParameters Parameters required for the useAbility function
     */
    public MoveCharacter(Player player, int maxPawns, int ID, int cost, int maxUses, List<RequestParameters> requestParameters) {
        super(player, maxPawns);
        isFirstUse = true;
        this.ID = ID;
        this.cost = cost;
        this.maxUses = maxUses;
        this.requestParameters = requestParameters;
    }

    /**
     * Setter for the bag
     * @param bag The Bag
     */
    public void setBag(Bag bag){
        this.bag = bag;
    }

    /**
     * Setter for the team
     * @param teams The teams of the game
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    /**
     * Places students on cards which in setup draw students from the bag
     */
    public  void  initialFill(){
        if(bag != null) {
            List<Student> students = bag.drawN(this.getMaxPawns());
            for(Student student : students){
                this.moveStudent(student);
            }
        }
    }

    /**
     * Method called when a Player tries to pay and use a Character
     * @param ID ID of the Character
     * @param player Player who is spending coins
     * @return A list of parameters which are needed to consequently use its ability
     * @throws IllegalArgumentException When wrong ID is given
     * @throws IllegalAccessException When it was already activated this turn
     */
    @Override
    public List<RequestParameters> useCharacter(int ID, Player player) throws IllegalArgumentException, IllegalStateException {
        if(this.ID != ID)
            throw new IllegalArgumentException("Trying to call character "+ID+" instead of"+this.ID);
        else if(usedThisTurn)
            throw new IllegalStateException("Already activated");
        else{
            this.playerUsing = player;
            if(isFirstUse)
                isFirstUse = false;
            usedThisTurn = true;
            //no need to reset uses, should be done before call (or else it won't even reach here)
        }
        return requestParameters;
    }

    /**
     * Returns the ID of the Character
     * @return the ID of the Character
     */
    @Override
    public int getCharacterID(){
        return  ID;
    }

    /**
     * True if it's the first time the Character is being used
     * @return isFirstUse
     */
    @Override
    public boolean isFirstUse() {
        return isFirstUse;
    }

    /**
     * True if Character was used (paid) this turn
     * @return usedThisTurn
     */
    @Override
    public boolean wasUsedThisTurn() {
        return usedThisTurn;
    }

    /**
     * The cost of the Character
     * @return The cost already incremented if this isn't its first use
     */
    @Override
    public int getCost() {
        return isFirstUse ? cost : cost + 1;
    }

    /**
     * Resets uses and sets usedThisTurn to false. Will be called once the turn ends
     */
    @Override
    public void resetUseState() {
        usedThisTurn = false;
        uses = 0;
    }

    /**
     * Uses the ability of Character 1
     * @param student
     * @param islandTile
     * @throws IllegalAccessException When no more uses are available
     */
    public void useAbilityC1(Student student, IslandTile islandTile) throws IllegalAccessException {
        if(uses < maxUses){
            islandTile.moveStudent(student);
            this.moveStudent(bag.drawN(1).get(0));
            uses++;
        }
        else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 7
     * @param studentFromCard
     * @param studentFromEntrance
     * @throws IllegalAccessException When no more uses are available
     */
    public void useAbilityC7(Student studentFromCard, Student studentFromEntrance) throws IllegalAccessException {
        if(uses < maxUses){
            StudentContainer entrance = studentFromEntrance.getStudentContainer();
            StudentContainer card = studentFromCard.getStudentContainer();
            entrance.removePawn(studentFromEntrance);
            card.removePawn(studentFromCard);
            entrance.placePawn(studentFromCard);
            card.placePawn(studentFromEntrance);
            uses++;
        }
        else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 10
     * @param studentFromEntrance
     * @param studentFromDiningRoom
     * @throws IllegalAccessException When no more uses are available
     */
    public void useAbilityC10(Student studentFromEntrance, Student studentFromDiningRoom) throws IllegalAccessException, FullTableException {
        if(uses < maxUses){
            StudentContainer entrance = studentFromEntrance.getStudentContainer();
            HashMap<Integer, Integer> studentToMove = new HashMap<>();
            studentToMove.put(studentFromEntrance.getID(), 0); //CHECKME: "0" should be changed. also below
            playerUsing.moveStudentsFromEntranceToDN(studentToMove);
            entrance.moveStudent(studentFromDiningRoom);
            uses++;
        }
        else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 11
     * @param studentFromCard
     * @throws IllegalAccessException When no more uses are available
     */
    public void useAbilityC11(Student studentFromCard) throws IllegalAccessException { //TODO: finish when adjusted PlayerBoard functions
        if(uses < maxUses){
            HashMap<Integer, Integer> studentToMove = new HashMap<>();
            studentToMove.put(studentFromCard.getID(), 0);
            uses++;
        }
        else throw new IllegalAccessException("No more uses available");
    }

    /**
     * Uses the ability of Character 12
     * @param color
     * @throws IllegalAccessException When no more uses are available
     */
    public void useAbilityC12(Color color) throws IllegalAccessException { //TODO: finish when adjusted PlayerBoard functions
        if(uses < maxUses){
            uses++;
        }
        else throw new IllegalAccessException("No more uses available");
    }
}
