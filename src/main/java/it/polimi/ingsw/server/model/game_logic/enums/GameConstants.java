package it.polimi.ingsw.server.model.game_logic.enums;

public enum GameConstants {
    MIN_NUMBER_OF_PLAYERS(2),
    MAX_NUMBER_OF_PLAYERS(4),
    NUMBER_OF_ISLANDS(12),
    MAX_NUMBER_OF_CARDS(10),
    DINING_ROOM_LANE_SIZE(10),
    INITIAL_STUDENTS_PER_COLOR(26)
    ;

    public final int value;

    GameConstants(int value) {
        this.value = value;
    }
}
