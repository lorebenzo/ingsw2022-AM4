package it.polimi.ingsw.server.model.game_logic;


import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.List;

public class LightGameState {

    public final List<Archipelago> archipelagos;
    public final List<SchoolBoard> schoolBoards;
    public final List<List<Color>> clouds;
    public final int currentPlayerSchoolBoardId;
    public final Phase currentPhase;
    public final List<Integer> roundOrder;
    public final Archipelago motherNaturePosition;

    public LightGameState(List<Archipelago> archipelagos, List<SchoolBoard> schoolBoards, List<List<Color>> clouds, int currentPlayerSchoolBoardId, Phase currentPhase, List<Integer> roundOrder, Archipelago motherNaturePosition) {
        this.archipelagos = archipelagos;
        this.schoolBoards = schoolBoards;
        this.clouds = clouds;
        this.currentPlayerSchoolBoardId = currentPlayerSchoolBoardId;
        this.currentPhase = currentPhase;
        this.roundOrder = roundOrder;
        this.motherNaturePosition = motherNaturePosition;
    }
}
