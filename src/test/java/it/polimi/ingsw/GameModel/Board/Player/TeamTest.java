package it.polimi.ingsw.GameModel.Board.Player;

import it.polimi.ingsw.GameModel.Board.Bag;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.GameModel.PlayerConfig;
import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Exceptions.FullTeamException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TeamTest {
    @Test
    public void addMemberTest() {

        PlayerConfig playerConfig = new PlayerConfig(2);
        Bag bag = new Bag();
        bag.fillRemaining();
        playerConfig.setBag(bag);

        List<Student> initialEntrance = new ArrayList<>();

        for(int i = 0; i < 7; i ++){
            Student student = new Student(Color.PINK, null);
            initialEntrance.add(student);
        }
        Team t = new Team(TowerColor.BLACK, 1);
        try {
            t.addMember("p0", playerConfig);
            t.addMember("p1", playerConfig);
        } catch (FullTeamException e){ e.printStackTrace(); }
        assert(t.getPlayerWithTowers().getNickname().equals("p0"));

        Assertions.assertThrows(FullTeamException.class,()-> t.addMember("p2", new PlayerConfig(4)));

        Team t1 = new Team(TowerColor.BLACK, 1);
        try {
            t1.addMember("p0", playerConfig);
        } catch (FullTeamException e){ e.printStackTrace(); assert(false);}
        Assertions.assertThrows(FullTeamException.class,()-> t.addMember("p1", new PlayerConfig(4)));

    }
}
