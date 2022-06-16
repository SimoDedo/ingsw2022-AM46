package it.polimi.ingsw.Network.Message.UserAction;


import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.util.List;

/**
 * User action sent when a user wants to use a character ability.
 */
public class UseAbilityUserAction extends UserAction{

    /**
     * The parameters requested by the active character that is being used.
     */
    private final List<Integer> requestedParameters;

    public UseAbilityUserAction(String sender, List<Integer> requestedParameters) {
        super(sender, UserActionType.USE_ABILITY);
        this.requestedParameters = requestedParameters;
    }

    public List<Integer> getRequestedParameters() {
        return requestedParameters;
    }
}
