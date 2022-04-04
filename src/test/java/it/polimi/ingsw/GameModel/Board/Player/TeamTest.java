package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TeamTest {
    @Test
    public void addMemberTest(){

        List<Student> initialEntrance = new ArrayList<>();

        for(int i = 0; i < 7; i ++){
            Student student = new Student(Color.PINK, null);
            initialEntrance.add(student);
        }
        Team t = new Team(TowerColor.BLACK, 4);
        try {
            t.addMember("p0", 4, initialEntrance);
            t.addMember("p1", 4, initialEntrance);
        } catch (FullTeamException e){ e.printStackTrace(); }
        assert(t.getPlayerWithTowers().getNickname().equals("p0"));

        Assertions.assertThrows(FullTeamException.class,()-> t.addMember("p2", 4, initialEntrance));

        Team t1 = new Team(TowerColor.BLACK, 2);
        try {
            t1.addMember("p0", 4, initialEntrance);
        } catch (FullTeamException e){ e.printStackTrace(); assert(false);}
        Assertions.assertThrows(FullTeamException.class,()-> t.addMember("p1", 4, initialEntrance));

    }
}
