package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

public class Table extends StudentContainer {
    private boolean firstCoin, secondCoin, thirdCoin;
    private final Color color;

    public Table(Player player, int maxPawns, Color c) {
        super(player, maxPawns);
        firstCoin = secondCoin = thirdCoin = false;
        color = c;
    }

    public Color getColor() {
        return color;
    }


    /**
     * Checks if the owner of the table is rewarded with a coin
     * @param student to place in the table
     * @throws FullTableException if the table cannot accept any more students
     * @throws IllegalArgumentException if the color of the student is not correct for the table (should never happen)
     */
    public void placeStudent(Student student) throws FullTableException, IllegalArgumentException {
        if(student.getColor() != getColor()){throw new IllegalArgumentException();}
        if((pawnCount() == 2 && !firstCoin) || (pawnCount() == 5 && !secondCoin) ||
                (pawnCount() == 8 && thirdCoin)){ getOwner().awardCoin(); }
        if(pawnCount() == 10){ throw new FullTableException(); }
        placePawn(student);
    }

    public int getScore(){
        return pawnCount();
    }

}
