package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.WizardType;

public class WizardInfo extends Info{

    private final String nickname;

    private final WizardType wizardType;

    public WizardInfo(String nickname, WizardType wizardType) {
        super("Wizard successfully chosen!");
        this.nickname = nickname;
        this.wizardType = wizardType;
    }

    public String getNickname() {
        return nickname;
    }

    public WizardType getWizardType() {
        return wizardType;
    }
}
