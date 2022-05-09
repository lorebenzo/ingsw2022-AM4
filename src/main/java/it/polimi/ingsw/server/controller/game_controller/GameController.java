package it.polimi.ingsw.server.controller.game_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.server.controller.game_state_controller.GameStateController;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;

import java.util.*;

public class GameController extends SugarMessageProcessor {
    public final UUID roomId;
    private final List<Player> players = new LinkedList<>();
    private final Map<UUID, Integer> upiToSchoolBoardId = new HashMap<>();
    private final int numPlayers;
    private final boolean expertMode;
    private boolean gameStarted = false;
    private GameStateController gameStateController = null;

    public GameController(UUID roomId, int numPlayers, boolean expertMode)
    {
        this.roomId = roomId;
        this.numPlayers = numPlayers;
        this.expertMode = expertMode;
    }

    private void addPlayerEffective(Player player) {
        synchronized (this.players) {
            this.players.add(player);
        }
    }

    public void addPlayer(Player player) {
        if(!this.gameStarted) this.addPlayerEffective(player);
    }

    public void startGame() throws GameStateInitializationFailureException {
        this.gameStarted = true;
        synchronized (this.players) {
            for (int i = 0; i < players.size(); i++)
                upiToSchoolBoardId.put(players.get(i).associatedPeer.upi, i);
        }
        this.gameStateController = new GameStateController(players.stream().map(player -> player.associatedPeer).toList());
    }


}
