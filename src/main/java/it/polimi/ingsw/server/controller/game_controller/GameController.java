package it.polimi.ingsw.server.controller.game_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.CommunicationController;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.server.repository.UsersRepository;

import java.util.*;

public class GameController extends SugarMessageProcessor {
    public final UUID roomId;
    private final List<Player> players = new LinkedList<>();
    private CommunicationController communicationController = null;
    private final UsersRepository usersRepository = UsersRepository.getInstance();
    private boolean gameStarted = false;

    public GameController(UUID roomId)
    {
        this.roomId = roomId;
    }

    private void addPlayerEffective(Player player) {
        this.players.add(player);
    }

    public void removePlayer(String username) {
        this.players.removeIf(player -> player.username.equals(username));
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public void addPlayer(Player player) {
        if(!this.gameStarted) this.addPlayerEffective(player);
    }

    public void startGame() throws GameStateInitializationFailureException {
        this.gameStarted = true;

        for (int i = 0; i < players.size(); i++) {
            usersRepository.saveUserSchoolBardMap(this.roomId, players.get(i).username, i);
        }

        this.communicationController = new CommunicationController(players);
    }

    public boolean containsPeer(Peer peer) {
        for(var player : this.players)
            if(player.associatedPeer.equals(peer)) return true;
        return false;
    }

    public boolean containsPlayer(String username) {
        for(var player : this.players)
            if(player.username.equals(username)) return true;
        return false;
    }

    public void updatePeerIfOlder(String username, Peer peer) {
        for(int i = 0; i < this.players.size(); i++) {
            var player = this.players.get(i);
            if (player.username.equals(username)) {
                if (!player.associatedPeer.upi.equals(peer.upi)) {
                    this.players.set(i, new Player(peer, username));
                }
                break;
            }
        }
    }

    public LightGameState getLightGameState() {
        return this.communicationController.getLightGameState();
    }

    @SugarMessageHandler
    public SugarMessage base(SugarMessage message, Peer peer) {
        return this.communicationController.process(message, peer);
    }
}
