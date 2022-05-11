package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.BoardElements.StudentContainer;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Exceptions.FullTableException;



public class Table extends StudentContainer {
    private final Boolean[] coins = {false, false, false};
    private final Color color;

    public Table(Player player, int maxPawns, Color c) {
        super(player, maxPawns);
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
     * @return true if coin is to be awarded
     */
    public boolean placeStudent(Student student) throws FullTableException, IllegalArgumentException{
        boolean giveCoin = false;
        switch(pawnCount()){
            case 2 -> { if(!coins[0]){ coins[0] = true; giveCoin = true; } }
            case 5 -> { if(!coins[1]){ coins[1] = true; giveCoin = true; } }
            case 8 -> { if(!coins[2]){ coins[3] = true; giveCoin = true; } }
        }
        if(pawnCount() == 10){ throw new FullTableException(); }
        placePawn(student);
        return giveCoin;
    }

    public int getScore(){
        return pawnCount();
    }

    public int getCoinsLeft() {
        int sum = 0;
        for(boolean b : coins){
            sum += b ? 1 : 0;
        }
        return sum;
    }

}
