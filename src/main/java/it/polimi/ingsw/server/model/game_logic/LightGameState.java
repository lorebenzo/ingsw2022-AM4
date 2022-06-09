package it.polimi.ingsw.server.model.game_logic;


import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.List;
import java.util.Map;

public class LightGameState {

    public final List<Archipelago> archipelagos;
    public final List<SchoolBoard> schoolBoards;
    public final List<List<Color>> clouds;
    public final int currentPlayerSchoolBoardId;
    public final Phase currentPhase;
    public final List<Integer> roundOrder;
    public final int motherNaturePosition;
    public Map<String, Integer> usernameToSchoolBoardID;
    public final Map<Integer, Card> schoolBoardIdsToCardPlayedThisRound;

    public LightGameState(List<Archipelago> archipelagos, List<SchoolBoard> schoolBoards, List<List<Color>> clouds, int currentPlayerSchoolBoardId, Phase currentPhase, List<Integer> roundOrder, Archipelago motherNaturePosition, Map<Integer, Card> schoolBoardIdsToCardPlayedThisRound) {
        this.archipelagos = archipelagos;
        this.schoolBoards = schoolBoards;
        this.clouds = clouds;
        this.currentPlayerSchoolBoardId = currentPlayerSchoolBoardId;
        this.currentPhase = currentPhase;
        this.roundOrder = roundOrder;
        this.motherNaturePosition = archipelagos.indexOf(motherNaturePosition);
        this.schoolBoardIdsToCardPlayedThisRound = schoolBoardIdsToCardPlayedThisRound;
    }

    public LightGameState addUsernames(Map<String, Integer> usernameToSchoolBoardID) {
        this.usernameToSchoolBoardID = usernameToSchoolBoardID;
        return this;
    }
}
