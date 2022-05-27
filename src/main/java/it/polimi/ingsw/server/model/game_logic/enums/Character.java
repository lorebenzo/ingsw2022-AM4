package it.polimi.ingsw.server.model.game_logic.enums;

public enum Character {
    CHARACTER_1(
            1,
            1,
            "Take 1 Student from this card and place it on an Island of your choice. Then, a new Student will be drawn from the Bag and will be placed on this card.",
            true),
    GET_PROFESSORS_WITH_SAME_STUDENTS(
            2,
            2,
            "During this turn, you take control of any number of Professors even if you have the same number of Students in your dining room as the player who currently controls them.",
            false),
    MOVE_MOTHER_NATURE_TO_ANY_ISLAND(
            3,
            3,
            "Choose an Island and resolve the Island as if Mother Nature had ended her movement there. Mother Nature will still move and the Island where she ends her movement will also be resolved.",
            false),
    TWO_ADDITIONAL_STEPS(
            4,
            1,
            "You may move Mother Nature up to 2 additional Islands than is indicated by the Assistant card you've played.",
            false),
    LOCK_ARCHIPELAGO(
            5,
            2,
            "Place a No Entry tile on an Island of your choice. The first time Mother Nature ends her movement there, the No Entry tile will return onto this card. No influences will be calculated on that Island and no Towers will be placed.",
            true),
    TOWERS_DONT_COUNT(
            6,
            3,
            "When resolving a Conquering on an Island, Towers do not count towards influence.",
            false),
    CHARACTER_7(
            7,
            1,
            "You may take up to 3 Students from this card and replace them with the same number of Students from your Entrance.",
            true),
    TWO_ADDITIONAL_INFLUENCE(
            8,
            2,
            "During the influence calculation this turn, you count as having 2 more influence.",
            false),
    CHARACTER_9(
            9,
            3,
            "Choose a color of Student: during the influence calculation this turn, that color adds no influence.",
            false),
    CHARACTER_10(
            10,
            1,
            "You may exchange up to 2 Students between your Entrance and your Dining Room.",
            false),
    CHARACTER_11(
            11,
            2,
            "Take 1 Student from this card and place it in your Dining Room. Then, draw a new Student from the Bag and place it on this card.",
            true),
    PUT_THREE_STUDENTS_IN_THE_BAG(
            12,
            3,
            "Choose a type of Student: every player (including yourself) must return 3 Students of that type from their Dining Room to the bag." +
                    "If any player has fewer than 3 Student of that type, they return as many Students as they have.",
            false);

    public final int characterId;
    public final int initialCost;
    public final String effect;
    public final boolean isStateful;

    Character(int characterId, int initialCost, String effect, boolean isStateful) {
        this.characterId = characterId;
        this.initialCost = initialCost;
        this.effect = effect;
        this.isStateful = isStateful;
    }

}
