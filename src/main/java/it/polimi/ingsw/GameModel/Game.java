package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.Player.TeamsFactory;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Game {
    private List<Team> teams;
    private int players;
    private int currentPlayerNumber;
    private final Player neutral = new Player();

    private Bag bag;

    public void initializeTeams(int players){
        teams = TeamsFactory.create(players);
    }

    public void addPlayer(String nick) throws FullTeamException, IllegalStateException {

        int studentsToDraw;

        switch (players){
            case 2: case 4: studentsToDraw = 7; break;
            case 3: studentsToDraw = 9; break;
            default:
                throw new IllegalStateException("Too many players: " + players);
        }

        teams.get(currentPlayerNumber % players + 1).addMember(nick, players, bag.draw(studentsToDraw));

        currentPlayerNumber ++;
    }

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

    public void playAssistant(String nick, int assistantID) throws NoSuchElementException{
        getPlayerByNick(nick).playAssistant(assistantID);
    }
}
