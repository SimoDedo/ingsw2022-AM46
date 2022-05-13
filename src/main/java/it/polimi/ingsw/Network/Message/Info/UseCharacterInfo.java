package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.RequestParameter;

import java.util.List;

public class UseCharacterInfo extends Info{

    private final int characterActivated;

    private final int maxUses;

    private final List<RequestParameter> requestParameters;

    public UseCharacterInfo(int characterActivated, int maxUses, List<RequestParameter> requestParameters) {
        super("Character successfully used!");
        this.characterActivated = characterActivated;
        this.maxUses = maxUses;
        this.requestParameters = requestParameters;
    }

    public int getCharacterActivated() {
        return characterActivated;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }
}
