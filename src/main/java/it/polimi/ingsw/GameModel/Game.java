package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CloudTile;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.Player.TeamsFactory;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;

import java.util.*;

public class Game {

    private int players; // deprecated
    private int currentPlayerNumber; // deprecated
    private final Player neutralPlayer = new Player();

    private Bag bag = new Bag();
    private Archipelago archipelago = new Archipelago();
    private List<CloudTile> clouds = new ArrayList<>();
    private ProfessorSet professorSet = new ProfessorSet();
    private List<Team> teams = new ArrayList<>();

    public Game(GameConfig gameConfig, Map<String, TowerColor> teamComposition) {

        archipelago.initialStudentPlacement(bag.drawN(10));
        bag.fillRemaining();
        gameConfig.getPlayerConfig().setBag(bag);

        for (int i = 0; i < gameConfig.getNumOfClouds(); i++)
            clouds.add(new CloudTile(gameConfig.getCloudSize(), bag));

        // the following code can be brought to an ad-hoc class: TeamManager.
        int teamSize = gameConfig.getNumOfPlayers()/2;
        Team white = new Team(TowerColor.WHITE, teamSize);
        Team black = new Team(TowerColor.BLACK, teamSize);
        Team grey;
        if (gameConfig.getNumOfPlayers() == 3) grey = new Team(TowerColor.GREY, teamSize);
        else grey = null;

        try {
            for (Map.Entry<String, TowerColor> entry : teamComposition.entrySet()) {
                switch (entry.getValue()) {
                    case WHITE:
                        white.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                    case BLACK:
                        black.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                    case GREY:
                        grey.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                }
            }
        } catch (FullTeamException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    //todo: complete game methods

    public void pushThisRoundInLastRound() {

    }

    public void assignWizard() {

    }

    public void playAssistant() {

    }

    private void checkDesperate() {

    }










    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param nick name of the player who is requesting the movement
     * @param studentDestinations Map of Student IDs to either a table (0) or an island ID.
     */
    public void moveStudentsFromEntrance(String nick, HashMap<Integer, Integer> studentDestinations){
        // check it's actually the players turn
        HashMap<Student, Integer> studentsToIslands =
                getPlayerByNick(nick).moveStudentsFromEntranceToDN(studentDestinations);
        // move students in hashmap into corresponding islands


    }

    public Player getPlayerByNick(String nick) throws NoSuchElementException {
        Player player = null;
        try{
            for(Team t : teams) { player = t.getPlayerByNick(nick); }
            } catch (NoSuchElementException e) {e.printStackTrace();}
        if (player == null) throw new NoSuchElementException();
        return player;
    }

    public void playAssistant(String nick, int assistantID) throws NoSuchElementException {
        getPlayerByNick(nick).playAssistant(assistantID);
    }
}
