package it.polimi.ingsw.server.model.game_logic;


import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.ArrayList;
import java.util.LinkedList;

public record LightGameState(
        LinkedList<Archipelago> archipelagos,
        ArrayList<SchoolBoard> schoolBoards,
        ArrayList<ArrayList<Color>> clouds,
        int currentPlayerSchoolBoardId,
        Phase currentPhase,
        LinkedList<Integer> roundOrder,
        Archipelago motherNaturePosition
) {
}
