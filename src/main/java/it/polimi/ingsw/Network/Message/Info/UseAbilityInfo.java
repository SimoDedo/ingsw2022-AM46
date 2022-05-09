package it.polimi.ingsw.Network.Message.Info;

public class UseAbilityInfo extends Info{

    private int characterActivated;

    private int usesLeft;

    public UseAbilityInfo(int characterActivated, int usesLeft) {
        super("Ability successfully used!");
        this.characterActivated = characterActivated;
        this.usesLeft = usesLeft;
    }

    public int getCharacterActivated() {
        return characterActivated;
    }

    public int getUsesLeft() {
        return usesLeft;
    }
}
