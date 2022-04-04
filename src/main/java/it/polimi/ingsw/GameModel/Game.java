package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CloudTile;
import it.polimi.ingsw.GameModel.Board.Player.AssistantCard;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.Player.Table;
import it.polimi.ingsw.GameModel.Board.Player.Team;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.GameModel.TurnManager;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;

import java.util.*;

public class Game {

    private final Player neutralPlayer = new Player();
    private Bag bag = new Bag();
    private Archipelago archipelago = new Archipelago();
    private List<CloudTile> clouds = new ArrayList<>();
    private ProfessorSet professorSet = new ProfessorSet();
    private List<Team> teams = new ArrayList<>();
    private TurnManager turnManager = new TurnManager();
    private Map<Player, AssistantCard> cardsPlayedThisRound = new LinkedHashMap<>();

    public Game(GameConfig gameConfig, Map<String, TowerColor> teamComposition) {

        archipelago.initialStudentPlacement(bag.drawN(10));
        bag.fillRemaining();
        gameConfig.getPlayerConfig().setBag(bag);

        for (int i = 0; i < gameConfig.getNumOfClouds(); i++)
            clouds.add(new CloudTile(gameConfig.getCloudSize(), bag));

        // the following code can be (almost, except for the TurnManager part) moved to an ad-hoc class: TeamManager.
        int teamSize = gameConfig.getNumOfPlayers() / 2;
        Team white = new Team(TowerColor.WHITE, teamSize);
        Team black = new Team(TowerColor.BLACK, teamSize);
        teams.add(white);
        teams.add(black);
        Team grey;
        if (gameConfig.getNumOfPlayers() == 3) {
            grey = new Team(TowerColor.GREY, teamSize);
            teams.add(grey);
        } else grey = null;

        Player newPlayer = null;
        try {
            for (Map.Entry<String, TowerColor> entry : teamComposition.entrySet()) {
                switch (entry.getValue()) {
                    case WHITE:
                        newPlayer = white.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                    case BLACK:
                        newPlayer = black.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                    case GREY:
                        newPlayer = grey.addMember(entry.getKey(), gameConfig.getPlayerConfig());
                        break;
                }
                turnManager.addPlayerClockwise(newPlayer);
            }
        } catch (FullTeamException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        Player player = null;
        for (Team team : teams) {
            try {
                player = team.getPlayerByNickname(nickname);
            } catch (NoSuchElementException ignored) {
            }
        }
        if (player == null) throw new NoSuchElementException("Player not found");
        return player;
    }

    private List<WizardType> getWizardTypes() { // can be made using streams
        List<WizardType> wizardTypes = new ArrayList<>();
        for (Team team : teams) {
            for (Player player : team.getMembers()) {
                wizardTypes.add(player.getWizardType());
            }
        }
        return wizardTypes;
    }

    public void assignWizard(String nickname, WizardType wizardType) throws NoSuchElementException {
        if (getWizardTypes().contains(wizardType)) throw new IllegalArgumentException("Wizard type already chosen");
        Player player = getPlayerByNickname(nickname);
        player.pickWizard(wizardType);
    }

    public void determineFirstRoundOrder() {
        turnManager.determinePlanningOrder();
        turnManager.nextPhase(); // set from idle -> planning
    }

    public String getCurrentPlayer() {
        return turnManager.getCurrentPlayer().getNickname();
    } // could be useful to controller

    public void playAssistant(String nickname, int assistantID) {
        if (!checkDesperate(nickname)) {
            for (AssistantCard assistantCard : cardsPlayedThisRound.values()) {
                if (assistantCard.getID() == assistantID)
                    throw new IllegalArgumentException("Assistant card already chosen");
            }
        }
        AssistantCard assistantPlayed = getPlayerByNickname(nickname).playAssistant(assistantID); // doesn't treat desperate
        cardsPlayedThisRound.put(getPlayerByNickname(nickname), assistantPlayed);
    }

    private boolean checkDesperate(String nickname) {
        return getPlayerByNickname(nickname).checkDesperate(cardsPlayedThisRound.values());
    }

    public void moveStudentFromEntrance(String nickname, int studentID, int containerID) {
        Player player = getPlayerByNickname(nickname);
        Student student = player.getStudentFromEntrance(studentID); // will have to throw exception if not present
        Table potentialTable = player.getTable(student.getColor());
        if (potentialTable.getID() == containerID) potentialTable.moveStudent(student);
        else {
            archipelago.moveStudent(student, archipelago.getIslandTileByID(containerID)); // will have to throw exception
        }
        checkAndMoveProfessor(student.getColor());
    }

    public void checkAndMoveProfessor(Color color) {
    } //this calls a strategy! todo: calls professorset.setowner when winner is found

    public void moveMotherNature(String nickname, int islandTileID) {
    } //todo: receives movecount from cardsplayedthisround, calls archipelago.movemothernature(islandtileid, movecount). finally calls resolveislandgroup

    public void resolveIslandGroup(IslandGroup islandGroup) throws GameOverException {
        archipelago.resolveIslandGroup(islandGroup, teams, professorSet);
    }

    public void takeFromCloud(String nickname, int cloudID) {
        for (CloudTile cloud : clouds) {
            if (cloud.getID() == cloudID) {
                List<Student> studentsTaken = cloud.removeAll();
                getPlayerByNickname(nickname).addToEntrance(studentsTaken);
            }
        }
    }

    public void determinePlanningOrder() {
        turnManager.determinePlanningOrder();
    }

    public void determineActionOrder() {
        turnManager.determineActionOrder(cardsPlayedThisRound);
    }

    public void pushThisRoundInLastRound() {

    }
}
