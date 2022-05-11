package it.polimi.ingsw.Utils.Enum;

public enum Characters {
    C1("In setup, draw 4 Students and place them on this card. EFFECT: Take 1 Student from this card and place it on an Island of your choice. Then, draw a new Student from the Bag and place it on this card."),
    C2("EFFECT: During this turn, you take control of any number of Professors even if you have the same number of Students as the player who currently controls them."),
    C3("EFFECT: Choose an Island and resolve the Island as if Mother Nature had ended her movement there. Mother Nature will still move and the Island where she ends her movement will also be resolved."),
    C4("EFFECT: You may move Mother Nature up to 2 additional Islands than is indicated by the Assistant card you've played."),
    C5("In Setup, put the 4 No Entry tiles on this card. EFFECT: Place a No Entry tile on an Island of your choice. The first time Mother Nature ends her movement there, put the No Entry tile back onto this card. DO NOT calculate influence on that Island, or place any Towers."),
    C6("EFFECT: When resolving a Conquering on an Island, Towers do not count towards influence."),
    C7("In Setup, draw 6 Students and place them on this card.You may take up to 3 Students from this card and replace them with the same number of students from your entrance."),
    C8("EFFECT: During the influence calculation this turn, you count as having 2 more influence."),
    C9("EFFECT: Choose a color of Student: during the influence calculation this turn, that color adds no influence."),
    C10("You may exchange up to 2 Students between your Entrance and your Dining Room."),
    C11("In Setup, draw 4 Students and place them on this card. Take 1 Student  from this card and place it in your Dining Room. Then, draw a new Student from the Bag and place it on this card."),
    C12("Choose a type of Student: every player (including yourself) must return 3 Students of that type from their Dining Room to the bag. If any player has fewer than 3 Students of that type, return as many Students as they have.");

    public String description;
    Characters(String description){
        this.description = description;
    }
}
