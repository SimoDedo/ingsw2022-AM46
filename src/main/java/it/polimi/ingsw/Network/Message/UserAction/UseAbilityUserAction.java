package it.polimi.ingsw.Network.Message.UserAction;


import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.util.List;

public class UseAbilityUserAction extends UserAction{

    private List<Integer> requestedParameters;

    public UseAbilityUserAction(String sender, List<Integer> requestedParameters) {
        super(sender, UserActionType.USE_ABILITY);
        this.requestedParameters = requestedParameters;
    }

    public List<Integer> getRequestedParameters() {
        return requestedParameters;
    }
}
