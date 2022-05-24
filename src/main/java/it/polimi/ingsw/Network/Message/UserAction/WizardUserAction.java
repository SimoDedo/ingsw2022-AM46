package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.Utils.Enum.WizardType;

public class WizardUserAction extends UserAction{

    private final WizardType wizardType;

    public WizardUserAction(String sender, WizardType wizardType) {
        super(sender, UserActionType.WIZARD);
        this.wizardType = wizardType;
    }

    public WizardType getWizardType() {
        return wizardType;
    }
}
