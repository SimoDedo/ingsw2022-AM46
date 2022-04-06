package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.Board.Archipelago.Archipelago;
import it.polimi.ingsw.GameModel.Board.Archipelago.IslandTile;
import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LambdaTest {
    Archipelago archipelago;
    Bag bag;
    CharacterTest characterTest;
    Consumer<List<Integer>> C1 = (list) -> {
        Student student = characterTest.getPawnByID(list.get(0));
        IslandTile islandTile = archipelago.getIslandTileByID(list.get(1));
        StudentContainer card = student.getStudentContainer();
        islandTile.moveStudent(student);
        card.moveStudent(bag.drawN(1).get(0));
    };

    BiConsumer<Student, IslandTile> char1 = (student, islandTile) -> {
        StudentContainer card = student.getStudentContainer();
        islandTile.moveStudent(student);
        card.moveStudent(bag.drawN(1).get(0));
    };

    BiConsumer<Student, Student> char2 = (studentFromCard, studentFromEntrance) ->{
        StudentContainer entrance = studentFromEntrance.getStudentContainer();
        StudentContainer card = studentFromCard.getStudentContainer();
        entrance.removePawn(studentFromEntrance);
        card.removePawn(studentFromCard);
        entrance.placePawn(studentFromCard);
        card.placePawn(studentFromEntrance);
    };

    public LambdaTest(Archipelago archipelago, Bag bag, CharacterTest characterTest) {
        this.archipelago = archipelago;
        this.bag = bag;
        this.characterTest = characterTest;
    }

    public Consumer<List<Integer>> getC1() {
        return C1;
    }
}
