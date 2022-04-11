package it.polimi.ingsw.server.communication.stubs;

import it.polimi.ingsw.server.game_logic.GameState;
import it.polimi.ingsw.server.game_logic.exceptions.GameStateInitializationFailureException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private GameState gameState;
    private final Controller controller;
    Map<Player, Integer> playerToSchoolBoardId;

    public Game(List<Player> playerList) throws GameStateInitializationFailureException {
        this.gameState = new GameState(playerList.size());
        this.controller = new Controller();
        playerToSchoolBoardId = new HashMap<>();
        for(int i = 0; i < playerList.size(); i++)
            playerToSchoolBoardId.put(playerList.get(i), i);
    }
}
