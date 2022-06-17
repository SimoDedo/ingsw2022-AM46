package it.polimi.ingsw.Network.Message.UserAction;

import it.polimi.ingsw.Utils.Enum.UserActionType;
import it.polimi.ingsw.Utils.Enum.WizardType;

/**
 * User action sent when a user wants to choose a wizard.
 */
public class WizardUserAction extends UserAction{

    /**
     * The wizard chosen.
     */
    private final WizardType wizardType;

    public WizardUserAction(String sender, WizardType wizardType) {
        super(sender, UserActionType.WIZARD);
        this.wizardType = wizardType;
    }

    public WizardType getWizardType() {
        return wizardType;
    }
}
