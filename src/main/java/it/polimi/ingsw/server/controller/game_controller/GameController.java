package it.polimi.ingsw.server.controller.game_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.CommunicationController;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.server.repository.GamesRepository;
import it.polimi.ingsw.server.repository.UsersRepository;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

public class GameController extends SugarMessageProcessor {
    public final UUID roomId;
    private final List<Player> players = new LinkedList<>();
    private final boolean isExpertMode;
    private CommunicationController communicationController = null;
    private boolean gameStarted = false;


    public GameController(UUID roomId, boolean isExpertMode)
    {
        this.roomId = roomId;
        this.isExpertMode = isExpertMode;
    }

    public GameController(UUID gameUUID, List<Pair<String, Integer>> players, boolean isExpertMode) throws GameStateInitializationFailureException {
        this(gameUUID, isExpertMode);
        players.forEach(player -> addPlayer(new Player(null, player.getValue0())));
        this.gameStarted = true;
        this.communicationController = CommunicationController.createCommunicationController(players, this.isExpertMode, gameUUID);
    }

    private void addPlayerEffective(Player player) {
        this.players.add(player);
    }

    public int activePlayers() {
        return (int) this.players.stream()
                .filter(p -> p.associatedPeer != null)
                .count();
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

    public void startGame() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        this.gameStarted = true;

        this.communicationController = CommunicationController.createCommunicationController(this.players, this.isExpertMode);
    }

    /**
     * Return true if there is the player in the game
     * @param peer of the player to check
     * @return true if the players list contains the peer provided
     */
    public boolean containsPeer(Peer peer) {
        for(var player : this.players)
            if(player.associatedPeer != null && player.associatedPeer.equals(peer)) return true;
        return false;
    }

    /**
     * Return true if there is the player in the game
     * @param username of the player to check
     * @return true if the players list contains the player provided
     */
    public boolean containsPlayer(String username) {
        for(var player : this.players)
            if(player.username.equals(username)) return true;
        return false;
    }

    /**
     * Returns a peer associated to the username provided
     * @param username of the player
     * @return an Optional<Peer> that contains the peer, or it can be empty
     */
    public Optional<Peer> getPeerFromPlayer(String username) {
        return this.players.stream()
                .filter(player -> player.username.equals(username))
                .map(player -> player.associatedPeer)
                .findFirst();
    }

    /**
     * Used when a player plays a move, it checks if the user has logged In from a new connection,
     * if yes, it updates the peer socket
     * @param username of the player
     * @param peer to check if it has changed
     */
    public void updatePeerIfOlder(@NotNull String username, @NotNull Peer peer) {
        for(int i = 0; i < this.players.size(); i++) {
            var player = this.players.get(i);
            if (player.username.equals(username)) {
                if (player.associatedPeer == null || !player.associatedPeer.upi.equals(peer.upi)) {
                    this.players.set(i, new Player(peer, username));
                }
                break;
            }
        }
    }

    /**
     * Returns the peers of the player's team
     * @param username of the player
     * @return a List<Peer> that contains the player's team peers
     */
    public List<Peer> getTeamPeers(String username) {
        return this.communicationController.getTeamUsernames(username).stream()
                // Get peer from player username
                .map(this::getPeerFromPlayer)
                // Filter only for the valid players (expected 1)
                .filter(Optional::isPresent)
                // Get the peer
                .map(Optional::get)
                .toList();
    }


    public LightGameState getLightGameState() {
        return this.communicationController.getLightGameState();
    }

    public UUID getGameUUID() {
        return this.communicationController.getGameUUID();
    }

    @SugarMessageHandler
    public SugarMessage base(SugarMessage message, Peer peer) {
        return this.communicationController.process(message, peer);
    }

    public void setInactivePlayer(Peer peer) {
        var player = this.players.stream()
                .filter(p -> p.associatedPeer != null && p.associatedPeer.equals(peer))
                .findFirst();

        player.ifPresent(p -> p.associatedPeer = null);
    }
}
