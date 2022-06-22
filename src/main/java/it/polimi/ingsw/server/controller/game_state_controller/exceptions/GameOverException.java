package it.polimi.ingsw.server.controller.game_state_controller.exceptions;

import java.util.Map;

public class GameOverException extends Exception{
    public Map<Integer, Boolean> schoolBoardIdToWinnerMap;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public GameOverException(Map<Integer, Boolean> schoolBoardIdToWinnerMap) {
        this.schoolBoardIdToWinnerMap = schoolBoardIdToWinnerMap;
    }
}
