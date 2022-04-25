package it.polimi.ingsw.GameModel.Board.CheckAndMoveProfessorStrategy;

import it.polimi.ingsw.GameModel.BoardElements.Professor;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.PlayerList;

/**
 * Interface for the checkAndMoveProfessor strategy. All strategies should implement this interface.
 */
public interface CheckAndMoveProfessorStrategy {

    void checkAndMoveProfessor(Professor prof, PlayerList playerList, Color color);
}
