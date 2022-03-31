package it.polimi.ingsw.GameModel.Board;

import it.polimi.ingsw.GameModel.Board.Player.Player;
import it.polimi.ingsw.GameModel.BoardElements.PawnContainer;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Bag extends StudentContainer {

    /**
     * Creates StudentContainer with owner and maxPawns
     *
     * @param player   the player who owns the container
     * @param maxPawns the max number of pawns the container can hold
     */
    public Bag(Player player, int maxPawns) {
        super(player, maxPawns);
    }

    public List<Student> draw(int amount){
        List<Student> drawnStudents = new ArrayList<>();
        IntStream.range(0, amount).forEach((x) -> drawnStudents.add(removePawnByIndex(0)));
        return drawnStudents;
    }
}
