package it.polimi.ingsw.GameModel.Board.Archipelago;

import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC6;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.Player.TeamManager;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.GameConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test archipelago class, focusing on State changing methods
 */
class ArchipelagoTest {

    /**
     * Test that given random starting island, method correctly distributes students
     */
    @RepeatedTest(20)
    void initialStudentPlacement(){
        Archipelago archipelago = new Archipelago();
        int idxMN = archipelago.getMotherNatureIslandGroupIndex();
        int idxOppositeMN = idxMN + 6 > 11 ? idxMN + 6 - 12 : idxMN + 6;
        Bag bag = new Bag();
        int randCheck = idxMN;
        Random random = new Random(89);
        while (randCheck == idxMN || randCheck == idxOppositeMN){
            randCheck = random.nextInt(12);
        }
        assertTrue(archipelago.getStudentsIDs().get(idxMN).size() == 0 &&
                archipelago.getStudentsIDs().get(randCheck).size() == 0);
        archipelago.initialStudentPlacement(bag.drawN(10));
        assertTrue(archipelago.getStudentsIDs().get(idxMN).size() == 0 &&
                archipelago.getStudentsIDs().get(idxOppositeMN).size() == 0 &&
                archipelago.getStudentsIDs().get(randCheck).size() == 1);
    }

    //region Resolve/Conquer/Merge testing
    /**
     * Tests that resolving an Island causes correct team to control the IslandGroup
     * @throws GameOverException not tested
     */
    @Test
    void resolveIslandGroup() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        assertTrue(archipelago.getTowerColorOfIslandGroup(0) == null && archipelago.getIslandGroupSize() == 12);
        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getTowerColorOfIslandGroup(0).equals(TowerColor.BLACK));
        Random random = new Random(89);
        assertTrue(archipelago.getTowerColorOfIslandGroup(random.nextInt(11 + 1)) == null);
    }

    /**
     * Tests that resolving an Island with tie causes no team to swap towers
     * @throws GameOverException not tested
     */
    @Test
    void resolveIslandGroupTie() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(0).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getTowerColorOfIslandGroup(0) == null);
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getTowerColorOfIslandGroup(0).equals(TowerColor.BLACK));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getTowerColorOfIslandGroup(0).equals(TowerColor.BLACK));
    }

    /**
     * Tests that resolving an Island causes correct team to control the IslandGroup and correctly merges
     * @throws GameOverException
     */
    @Test
    void resolveIslandGroupMerge() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(1).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(2).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(0, players,professorSet);
        archipelago.resolveIslandGroup(2, players,professorSet);
        archipelago.resolveIslandGroup(1, players,professorSet);
        assertTrue(archipelago.getIslandGroupSize() == 10);
        assertTrue(archipelago.getIslandTilesIDs().get(0).size() == 3);

    }

    /**
     * Tests that resolving an Island causes correct team to control the IslandGroup and correctly merges when island at corners
     * @throws GameOverException
     */
    @Test
    void resolveIslandGroupMergeCorner() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(11).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(1).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(11, players,professorSet);
        archipelago.resolveIslandGroup(1, players,professorSet);
        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getIslandGroupSize() == 10);
        assertTrue(archipelago.getIslandTilesIDs().get(0).size() == 3);

    }

    /**
     * Tests that resolving an Island causes correct team to control the IslandGroup and correctly merges when island at corners
     * @throws GameOverException
     */
    @Test
    void resolveIslandGroupMergeCorner2() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(10).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(11).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(10, players,professorSet);
        archipelago.resolveIslandGroup(0, players,professorSet);
        archipelago.resolveIslandGroup(11, players,professorSet);
        assertTrue(archipelago.getIslandGroupSize() == 10);
        assertTrue(archipelago.getIslandTilesIDs().get(archipelago.getIslandGroupSize()-1).size() == 3); //Merge on last IslandGroup, thus check on last after merge
    }

    /**
     * Tests that resolving an Island causes correct team to control the IslandGroup and correctly merges multiple island on a complex situation
     * @throws GameOverException
     */
    @Test
    void resolveIslandGroupMergeMany() throws GameOverException {
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(1).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(2).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(3).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(4).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(5).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(6).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(7).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(8).get(0));
        archipelago.placeStudent(new Student(Color.RED, null),archipelago.getIslandTilesIDs().get(9).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(10).get(0));
        archipelago.placeStudent(new Student(Color.PINK, null),archipelago.getIslandTilesIDs().get(11).get(0));

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(11, players,professorSet);
        archipelago.resolveIslandGroup(0, players,professorSet);
        archipelago.resolveIslandGroup(1, players,professorSet);
        archipelago.resolveIslandGroup(1, players,professorSet);
        archipelago.resolveIslandGroup(2, players,professorSet);
        archipelago.resolveIslandGroup(3, players,professorSet);
        archipelago.resolveIslandGroup(3, players,professorSet);
        archipelago.resolveIslandGroup(4, players,professorSet);
        archipelago.resolveIslandGroup(4, players,professorSet);
        archipelago.resolveIslandGroup(4, players,professorSet);
        archipelago.resolveIslandGroup(5, players,professorSet);
        archipelago.resolveIslandGroup(6, players,professorSet);
        archipelago.resolveIslandGroup(0, players,professorSet);
        assertTrue(archipelago.getIslandGroupSize() == 6);
        //System.out.println(archipelago.getIslandTilesIDs());
        assertTrue(archipelago.getIslandTilesIDs().get(5).size() == 4 &&
                archipelago.getTowerColorOfIslandGroup(5).equals(TowerColor.BLACK));
    }

    /**
     * Resolves IslandGroup using a strategy (C6)
     * @throws GameOverException
     */
    @Test
    void resolveIslandGroupStrategy() throws GameOverException {
        ResolveStrategyC6 resolveStrategyC6 = new ResolveStrategyC6();
        Archipelago archipelago = new Archipelago();
        archipelago.placeStudent(new Student(Color.PINK, null), archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.setResolveStrategy(resolveStrategyC6);

        Bag bag = new Bag();
        bag.fillRemaining();
        GameConfig gameConfig = new GameConfig(4);
        gameConfig.getPlayerConfig().setBag(bag);

        HashMap<String, TowerColor> teamConfiguration = new LinkedHashMap<String, TowerColor>();
        teamConfiguration.put("Simo", TowerColor.BLACK);
        teamConfiguration.put("Greg", TowerColor.BLACK);
        teamConfiguration.put("Pirovano", TowerColor.WHITE);
        teamConfiguration.put("Ceruti", TowerColor.WHITE);
        TeamManager teamManager = new TeamManager();
        PlayerList players = teamManager.create(gameConfig, teamConfiguration);

        ProfessorSet professorSet = new ProfessorSet();
        professorSet.setOwner(Color.PINK, players.getTeam(TowerColor.BLACK).get(0));
        professorSet.setOwner(Color.RED, players.getTeam(TowerColor.WHITE).get(0));

        archipelago.resolveIslandGroup(0, players, professorSet);
        archipelago.placeStudent(new Student(Color.RED, null), archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.placeStudent(new Student(Color.RED, null), archipelago.getIslandTilesIDs().get(0).get(0));
        archipelago.resolveIslandGroup(0, players, professorSet);
        assertTrue(archipelago.getTowerColorOfIslandGroup(0).equals(TowerColor.WHITE)); //Wins white despite Black having 1 student and 1
                                                                                                    // tower because of the strategy
    }
    //endregion

    @Test
    void getStudentByID(){
        Archipelago archipelago = new Archipelago();
        Student student = new Student(Color.RED, null);
        archipelago.placeStudent(student, archipelago.getIslandTilesIDs().get(0).get(0));
        assertTrue(archipelago.getStudentByID(student.getID()).equals(student));
    }
}