package it.polimi.ingsw.server.controller.game_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.CommunicationController;
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
    private CommunicationController communicationController = null;

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

    public void startGame() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        this.gameStarted = true;
        synchronized (this.players) {
            for (int i = 0; i < players.size(); i++)
                upiToSchoolBoardId.put(players.get(i).associatedPeer.upi, i);
        }
        this.communicationController = CommunicationController.createCommunicationController(players.stream().map(player -> player.associatedPeer).toList(),expertMode);
        //this.communicationController = new CommunicationController(players.stream().map(player -> player.associatedPeer).toList());
    }

    public boolean containsPeer(Peer peer) {
        for(var player : this.players)
            if(player.associatedPeer.equals(peer)) return true;
        return false;
    }

    @SugarMessageHandler
    public SugarMessage base(SugarMessage message, Peer peer) {
        System.out.println("Game controller: ");
        var ret = this.communicationController.process(message, peer);
        System.out.println(ret.serialize());
        return ret;
    }

}
