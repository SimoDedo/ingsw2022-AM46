package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void placeStudent(Student student) throws FullTableException, IllegalArgumentException{
        switch(pawnCount()){
            case 2:
                if(!firstCoin){ firstCoin = true; getOwner().awardCoin(); } break;
            case 5:
                if(!secondCoin){ secondCoin = true; getOwner().awardCoin(); } break;
            case 8:
                if(!thirdCoin){ thirdCoin = true; getOwner().awardCoin(); } break;
        }
        if(pawnCount() == 10){ throw new FullTableException(); }
        placePawn(student);
    }

    public int getScore(){
        return pawnCount();
    }

}
