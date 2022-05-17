package it.polimi.ingsw.server.model.game_logic;


import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.List;

public record LightGameState(
        List<Archipelago> archipelagos,
        List<SchoolBoard> schoolBoards,
        List<List<Color>> clouds,
        int currentPlayerSchoolBoardId,
        Phase currentPhase,
        List<Integer> roundOrder,
        Archipelago motherNaturePosition
) {
}
