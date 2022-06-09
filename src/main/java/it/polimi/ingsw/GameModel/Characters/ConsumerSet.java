package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandGroup;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Archipelago.MoveMotherNatureStrategy.MoveMotherNatureStrategyC4;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC6;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC8;
import it.polimi.ingsw.GameModel.Board.Archipelago.ResolveStrategy.ResolveStrategyC9;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy.CheckAndMoveProfessorStrategyC2;
import it.polimi.ingsw.GameModel.Board.CoinBag;
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;
import it.polimi.ingsw.Utils.Exceptions.LastRoundException;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Class that stores the different Consumer objects used by each Character to model their abilities.
 */
public class ConsumerSet {

    private final List<Consumer<List<Integer>>> consumers = new ArrayList<>();

    public ConsumerSet(Archipelago archipelago, Bag bag, PlayerList playerList, ProfessorSet professorSet,
                       List<AbstractCharacter> characters, CoinBag coinBag) {

        consumers.add((list) -> { // C1
            StudentMoverCharacter char1 = (StudentMoverCharacter) characters.get(0);
            Student student = char1.getPawnByID(list.get(0));
            IslandTile islandTile = archipelago.getIslandTileByID(list.get(1));
            islandTile.moveStudent(student);
            char1.placePawn(bag.draw());
            if(bag.pawnCount() == 0)
                throw  new LastRoundException("No more students in bag");
        });

        consumers.add((list) -> { // C2
            Player activator = characters.get(1).getOwner();
            professorSet.setCheckAndMoveProfessorStrategy(new CheckAndMoveProfessorStrategyC2(activator));
        });

        consumers.add((list) -> { // C3
            IslandGroup islandGroup = archipelago.getIslandTileByID(list.get(0)).getIslandGroup();
            archipelago.resolveIslandGroup(islandGroup, playerList, professorSet); //throws GameOverException
        });

        consumers.add((list) -> { // C4
            archipelago.setMotherNatureStrategy(new MoveMotherNatureStrategyC4());
        });

        consumers.add((list) -> { // C5
            NoEntryCharacter char5 = (NoEntryCharacter) characters.get(4);
            char5.removeNoEntryTile();
            IslandGroup islandGroup = archipelago.getIslandTileByID(list.get(0)).getIslandGroup();
            islandGroup.addNoEntryTile(char5);
        });

        consumers.add((list) -> { // C6
            archipelago.setResolveStrategy(new ResolveStrategyC6());
        });

        consumers.add((list) -> { // C7
            StudentMoverCharacter char7 = (StudentMoverCharacter) characters.get(6);
            Player activator = char7.getOwner();

            Student studentFromCharacter = char7.removePawnByID(list.get(0));
            Student studentFromEntrance = activator.removeStudentByID(list.get(1));

            activator.addToEntrance(studentFromCharacter);
            char7.placePawn(studentFromEntrance);
        });

        consumers.add((list) -> { // C8
            Player activator = characters.get(7).getOwner();
            archipelago.setResolveStrategy(new ResolveStrategyC8(activator));
        });

        consumers.add((list) -> { // C9
            int colorID = list.get(0);
            Color color = Arrays.stream(Color.values()).toList().get(colorID);
            archipelago.setResolveStrategy(new ResolveStrategyC9(color));
        });

        consumers.add((list) -> { // C10
            StudentMoverCharacter char9 = (StudentMoverCharacter) characters.get(9);
            Player activator = char9.getOwner();
            Student studentFromEntrance, studentFromDR;

            try { // entrance -> DR
                studentFromEntrance = activator.getStudentFromEntrance(list.get(0));
                if(activator.getTable(studentFromEntrance.getColor()).isFull())
                    throw new FullTableException("Can't add a student to a full table!");
                else{
                    studentFromEntrance = activator.removeStudentByID(list.get(0));
                    try { //try adding to DR
                        if(activator.addToDR(studentFromEntrance)){
                            try{ //Try awarding coin
                                coinBag.removeCoin();
                                activator.awardCoin();
                            }catch ( ArithmeticException e){e.printStackTrace();}
                        }
                    }
                    catch (FullTableException fte) { fte.printStackTrace(); }

                    studentFromDR = activator.removeStudentByID(list.get(1));
                    activator.addToEntrance(studentFromDR);
                }
            }
            catch (NoSuchElementException e) { // DR -> entrance (actually never used)
                studentFromDR = activator.removeStudentByID(list.get(0));
                activator.addToEntrance(studentFromDR);

                studentFromEntrance = activator.removeStudentByID(list.get(1));
                try { //try adding to DR
                    if(activator.addToDR(studentFromEntrance)){
                        try{//Try awarding coin
                            coinBag.removeCoin();
                            activator.awardCoin();
                        }catch (ArithmeticException ae){ae.printStackTrace();}
                    }
                }
                catch (FullTableException fte) { fte.printStackTrace(); }
            }

            professorSet.checkAndMoveProfessor(playerList, studentFromDR.getColor());
            professorSet.checkAndMoveProfessor(playerList, studentFromEntrance.getColor());
        });

        consumers.add((list) -> { // C11
            StudentMoverCharacter char11 = (StudentMoverCharacter) characters.get(10);
            Player activator = char11.getOwner();

            Student student = char11.removePawnByID(list.get(0));
            try { //try adding to DR
                if(activator.addToDR(student)){
                    try{ //try awarding coin
                        coinBag.removeCoin();
                        activator.awardCoin();
                    }catch ( ArithmeticException ae){ae.printStackTrace();}
                }
            } catch (FullTableException e) {e.printStackTrace();}

            professorSet.checkAndMoveProfessor(playerList, student.getColor());
            char11.placePawn(bag.draw());
            if(bag.pawnCount() == 0)
                throw  new LastRoundException("No more students in bag");
        });

        consumers.add((list) -> { // C12
            Color color;
            List<Student> removedStudents = new ArrayList<>();
            color = Arrays.stream(Color.values()).toList().get(list.get(0));
            for (Player player : playerList) {
                removedStudents.addAll(player.removeNFromDR(3, color));
            }
            bag.placePawns(removedStudents);

            professorSet.checkAndMoveProfessor(playerList, color);
        });
    }

    /**
     * Getter for the Consumer of the Character with the given character ID.
     * @param characterID the ID of the Character that needs this Consumer
     * @return the Consumer of that Character
     */
    public Consumer<List<Integer>> getConsumer(int characterID) {
        return consumers.get(characterID - 1);
    }




}
