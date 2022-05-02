package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.TowerColor;

public class TowerColorInfo extends Info{
    private String nickname;

    private TowerColor towerColorChosen;

    public TowerColorInfo(String nickname, TowerColor towerColorChosen) {
        super("Tower color successfully chosen!");
        this.nickname = nickname;
        this.towerColorChosen = towerColorChosen;
    }

    public String getNickname() {
        return nickname;
    }

    public TowerColor getTowerColorChosen() {
        return towerColorChosen;
    }


}
