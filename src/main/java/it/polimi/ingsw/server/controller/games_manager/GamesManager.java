package it.polimi.ingsw.server.controller.games_manager;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.exceptions.RoomNotFoundException;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageFromLowerLayersHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.game_state_controller.messages.GameOverMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.KOMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKAndUpdateMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.NotifyGameOverMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.controller.game_controller.GameController;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.utils.multilist.MultiList;

import java.io.IOException;
import java.util.*;

public class GamesManager extends SugarMessageProcessor {
    private final List<GameController> games = new LinkedList<>();
    private final SugarServer server;
    private final MultiList<Peer, Integer, Boolean> matchMakingList = new MultiList<>();
    private final Map<Peer, String> peerUsernameMap = new HashMap<>();

    public GamesManager(SugarServer server) {
        this.server = server;
    }

    @SugarMessageHandler
    public SugarMessage joinMatchMakingMsg(SugarMessage message, Peer peer) {
        var msg = (JoinMatchMakingMsg) message;

        this.peerUsernameMap.put(peer, AuthController.getUsernameFromJWT(msg.jwt));

        // Add the peer to the matchmaking room
        this.matchMakingList.add(peer, msg.numberOfPlayers, msg.expertMode);

        // Check if a game can be created
        this.createMatchIfPossible(msg.numberOfPlayers, msg.expertMode);

        return new OKMsg(ReturnMessage.JOIN_MATCHMAKING_SUCCESS.text);
    }

    private void createMatchIfPossible(int numberOfPlayers, boolean expertMode) {
        // Remove all the inactive peers from the matchmaking list
        Set<Peer> peersToRemove = new HashSet<>();
        this.matchMakingList.forEach((peer, numOfPlayers, expMode) -> {
            if(peer.peerSocket.isClosed()) peersToRemove.add(peer);
        });
        peersToRemove.forEach(this.matchMakingList::remove);

        // Filter the peers that are waiting in the matchmaking room, and they have the same match preferences as the
        // peer that just joined the room
        var filteredMatchMakingList = this.matchMakingList.filter(
                (peer, numPlayers, expMode) -> numPlayers == numberOfPlayers && expMode == expertMode
        );
        if(
            filteredMatchMakingList.size() == numberOfPlayers
        ) {
            // Start match
            var gameRoomId = this.server.createRoom();
            var gameController = new GameController(gameRoomId, numberOfPlayers, expertMode);
            // Add players to the game controller
            filteredMatchMakingList.forEach((peer, numPlayers, expMode) -> gameController.addPlayer(new Player(peer, this.peerUsernameMap.get(peer))));

            try {
                filteredMatchMakingList.forEach((peer, nPlayers, expMode) -> this.server.getRoom(gameRoomId).addPeer(peer));

                this.server.multicastToRoom(gameRoomId, new OKMsg(ReturnMessage.JOIN_GAME_SUCCESS.text));

                gameController.startGame();
                this.games.add(gameController);

                // Remove from the matchmaking peers that joined the game
                filteredMatchMakingList.forEach((peer, nPlayers, expMode) -> this.matchMakingList.remove(peer));
            } catch (IOException | RoomNotFoundException ignored) { }
            catch (GameStateInitializationFailureException e) {
                try {
                    this.server.multicastToRoom(gameRoomId, new KOMsg(ReturnMessage.DELETING_GAME.text));
                } catch (IOException | RoomNotFoundException ignored) { }
            }
        }
    }

    @SugarMessageHandler
    public SugarMessage base (SugarMessage sugarMessage, Peer peer) {
        var gameInvolvingPeer = findGameInvolvingPeer(peer);

        if(gameInvolvingPeer.isPresent()) {
            var ret = gameInvolvingPeer.get().process(sugarMessage, peer);

            // Process the message coming from the lower layers
            this.processFromLowerLayers(ret, peer);
        }

        return null;
    }

    private Optional<GameController> findGameInvolvingPeer(Peer peer) {
        return this.games
                .stream()
                .filter(gameController -> gameController.containsPeer(peer))
                .findFirst();
    }


    // Handling messages from lower layers
    @SugarMessageFromLowerLayersHandler
    public void gameOverMsg(SugarMessage message) { // From CommunicationController
        GameOverMsg msg = (GameOverMsg) message;

        Peer aPeerFromThisGame = null;

        // Notify clients
        for(var peer : msg.peerToIsWinner.keySet()) {
            aPeerFromThisGame = peer;
            try {
                this.server.send(new NotifyGameOverMsg(
                        msg.peerToIsWinner.get(peer) ? ReturnMessage.YOU_WIN.text : ReturnMessage.YOU_LOSE.text
                ).serialize(), peer.peerSocket);
            } catch (IOException ignored) { }
        }

        // Close game
        var game = this.findGameInvolvingPeer(aPeerFromThisGame);
        game.ifPresent(this.games::remove);
    }

    @SugarMessageFromLowerLayersHandler
    public void okAndUpdateMsg(SugarMessage message, Peer receiver) {
        var msg = (OKAndUpdateMsg) message;

        try {
            this.server.send(msg.okMsg.serialize(), receiver.peerSocket);

            var gameInvolvingReceiver = findGameInvolvingPeer(receiver);
            if(gameInvolvingReceiver.isPresent()) {
                this.server.multicastToRoom(gameInvolvingReceiver.get().roomId, msg.updateClientMsg);
            }
        } catch (IOException ignored) {} catch (RoomNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SugarMessageFromLowerLayersHandler
    public void baseLowerLayers(SugarMessage message, Peer receiver) {
        // Games manager had nothing to do with message coming from lower layers, so it will just forward the message to
        // the peer
        try {
            this.server.send(message.serialize(), receiver.peerSocket);
        } catch (IOException ignored) { }
    }
}
