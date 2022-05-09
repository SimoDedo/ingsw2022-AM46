package it.polimi.ingsw.Network.Message.Info;

public class PlayAssistantInfo extends Info{

    private String nickname;

    private int assistantPlayed;

    public PlayAssistantInfo(String nickname, int assistantPlayed) {
        super("Assistant successfully played");
        this.nickname = nickname;
        this.assistantPlayed = assistantPlayed;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAssistantPlayed() {
        return assistantPlayed;
    }
}
