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
import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.Board.ProfessorSet;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.GameOverException;
import it.polimi.ingsw.Utils.PlayerList;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Class that stores the different Consumer objects used by each Character to model their abilities.
 */
public class ConsumerSet {

    private final List<Consumer<List<Integer>>> consumers = new ArrayList<>();

    public ConsumerSet(Archipelago archipelago, Bag bag, PlayerList playerList, ProfessorSet professorSet,
                       List<AbstractCharacter> characters) {

        //todo: add exception handling maybe?

        consumers.add((list) -> { // C1
            StudentMoverCharacter char1 = (StudentMoverCharacter) characters.get(0);
            Student student = char1.getPawnByID(list.get(0));
            IslandTile islandTile = archipelago.getIslandTileByID(list.get(1));
            islandTile.moveStudent(student);
            char1.placePawn(bag.draw());
        });

        consumers.add((list) -> { // C2
            Player activator = ((StrategyCharacter) characters.get(1)).getOwner();
            professorSet.setCheckAndMoveProfessorStrategy(new CheckAndMoveProfessorStrategyC2(activator));
        });

        consumers.add((list) -> { // C3
            IslandGroup islandGroup = archipelago.getIslandTileByID(list.get(0)).getIslandGroup();
            try { archipelago.resolveIslandGroup(islandGroup, playerList, professorSet); }
            catch (GameOverException e) { e.printStackTrace(); }
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
            activator.addToEntrance(studentFromCharacter);

            Student studentFromEntrance = activator.removeStudentFromEntrance(list.get(1));
            char7.placePawn(studentFromEntrance);
        });

        consumers.add((list) -> { // C8
            archipelago.setResolveStrategy(new ResolveStrategyC8());
        });

        consumers.add((list) -> { // C9
            archipelago.setResolveStrategy(new ResolveStrategyC9());
        });

        consumers.add((list) -> { // C10
            StudentMoverCharacter char9 = (StudentMoverCharacter) characters.get(9);
            Player activator = char9.getOwner();
            Student studentFromEntrance, studentFromDR;

            try { // entrance -> DR
                studentFromEntrance = activator.removeStudentFromEntrance(list.get(0));
                activator.addToDR(studentFromEntrance);

                studentFromDR = activator.removeStudentFromDR(list.get(1));
                activator.addToEntrance(studentFromDR);
            }
            catch (NoSuchElementException e) { // DR -> entrance
                studentFromDR = activator.removeStudentFromDR(list.get(0));
                activator.addToEntrance(studentFromDR);

                studentFromEntrance = activator.removeStudentFromEntrance(list.get(1));
                activator.addToDR(studentFromEntrance);
            }

            professorSet.checkAndMoveProfessor(studentFromDR.getColor());
        });

        consumers.add((list) -> { // C11
            StudentMoverCharacter char11 = (StudentMoverCharacter) characters.get(10);
            Player activator = char11.getOwner();

            Student student = char11.removePawnByID(list.get(0));
            activator.addToDR(student);

            char11.placePawn(bag.draw());
        });

        consumers.add((list) -> { // C12
            Color color;
            List<Student> removedStudents = new ArrayList<>();
            color = switch (list.get(0)) {
                case 0 -> Color.YELLOW;
                case 1 -> Color.BLUE;
                case 2 -> Color.GREEN;
                case 3 -> Color.RED;
                case 4 -> Color.PINK;
                default -> throw new IllegalArgumentException("Invalid color");
            };
            for (Player player : playerList) {
                removedStudents.add(player.removeThreeFromDR(color));
            }
            bag.placePawns(removedStudents);

            professorSet.checkAndMoveProfessor(color);
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
