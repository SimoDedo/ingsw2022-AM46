package it.polimi.ingsw.View;

import it.polimi.ingsw.Network.Message.Info.Info;
import it.polimi.ingsw.Network.Message.UserAction.GameSettingsUserAction;
import it.polimi.ingsw.Network.Message.UserAction.TowerColorUserAction;
import it.polimi.ingsw.Network.Message.UserAction.WizardUserAction;
import it.polimi.ingsw.Utils.Enum.GameMode;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;

import java.util.Arrays;

public interface View {

    public void setNickname(String nickname);

    public String askLogin();

    public GameSettingsUserAction askGameSettings();

    public TowerColorUserAction askTowerColor(int numOfPlayers);

    public WizardUserAction askWizard();

    public void showInfo(Info info);
}
