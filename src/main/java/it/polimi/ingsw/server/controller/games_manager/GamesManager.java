package it.polimi.ingsw.server.controller.games_manager;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.exceptions.RoomNotFoundException;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageFromLowerLayersHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.games_manager.messages.ChatMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.games_manager.messages.GamesUpdateMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.GetGamesMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.controller.game_controller.GameController;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.utils.multilist.MultiList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class GamesManager extends SugarMessageProcessor {
    private final List<GameController> games = new LinkedList<>();
    private final SugarServer server;
    private final MultiList<Player, Integer, Boolean> matchMakingList = new MultiList<>();

    public GamesManager(SugarServer server) {
        this.server = server;
    }

    @SugarMessageHandler
    public SugarMessage getGamesMsg(SugarMessage message, Peer peer) {
        var msg = (GetGamesMsg) message;
        var username = AuthController.getUsernameFromJWT(msg.jwt);

        var gameController = this.games.stream()
                .filter(gc -> gc.containsPlayer(username))
                .findFirst();

        // Game controller UUID if present, or else is null
        var gameControllerUUID = gameController
                .map(controller -> controller.roomId)
                .orElse(null);

        return new GamesUpdateMsg(gameControllerUUID);
     }

    @SugarMessageHandler
    public SugarMessage joinMatchMakingMsg(SugarMessage message, Peer peer) {
        var msg = (JoinMatchMakingMsg) message;
        var username = AuthController.getUsernameFromJWT(msg.jwt);

        // if there is already the player in the matchmaking list, it removes him
        matchMakingList.remove(new Player(peer, username));

        // If player was already in another game, it removes it from the game
        this.games.stream()
                .filter(gc -> gc.containsPlayer(username))
                .findFirst()
                .ifPresent((gameController) -> gameController.removePlayer(username));

        // Add the peer to the matchmaking room
        this.matchMakingList.add(new Player(peer, username), msg.numberOfPlayers, msg.expertMode);

        // Check if a game can be created
        this.createMatchIfPossible(msg.numberOfPlayers, msg.expertMode);

        return new OKMsg(ReturnMessage.JOIN_MATCHMAKING_SUCCESS.text);
    }

    private void createMatchIfPossible(int numberOfPlayers, boolean expertMode) {
        // Remove all the inactive peers from the matchmaking list
        Set<Player> playersToRemove = new HashSet<>();
        this.matchMakingList.forEach((player, numOfPlayers, expMode) -> {
            if(player.associatedPeer.peerSocket.isClosed()) playersToRemove.add(player);
        });
        playersToRemove.forEach(this.matchMakingList::remove);

        // Filter the peers that are waiting in the matchmaking room, and they have the same match preferences as the
        // peer that just joined the room
        var filteredMatchMakingList = this.matchMakingList.filter(
                (peer, numPlayers, expMode) -> numPlayers == numberOfPlayers && expMode == expertMode
        );
        if( filteredMatchMakingList.size() == numberOfPlayers ) {
            // Start match
            var gameRoomId = this.server.createRoom();
            var gameController = new GameController(gameRoomId, expertMode);
            // Add players to the game controller
            filteredMatchMakingList.forEach((player, numPlayers, expMode) -> gameController.addPlayer(player));

            try {
                filteredMatchMakingList.forEach((player, nPlayers, expMode) -> this.server.getRoom(gameRoomId).addPeer(player.associatedPeer));

                gameController.startGame();
                this.gameLogicMulticast(gameController, new OKMsg(ReturnMessage.JOIN_GAME_SUCCESS.text));
                this.gameLogicMulticast(gameController, new UpdateClientMsg(gameController.getLightGameState()));

                this.games.add(gameController);

                // Remove from the matchmaking peers that joined the game
                filteredMatchMakingList.forEach((peer, nPlayers, expMode) -> this.matchMakingList.remove(peer));
            } catch (GameStateInitializationFailureException e) {
                try {
                    //todo: da fixare
                    this.server.multicastToRoom(gameRoomId, new KOMsg(ReturnMessage.DELETING_GAME.text));
                } catch (IOException | RoomNotFoundException ignored) { }
            } catch (EmptyStudentSupplyException e) {
                e.printStackTrace();
            }
        }
    }

    @SugarMessageHandler
    public SugarMessage base (SugarMessage sugarMessage, Peer peer) {
        var username = AuthController.getUsernameFromJWT(sugarMessage.jwt);
        var gameInvolvingPlayer = findGameInvolvingPlayer(username);

        if(gameInvolvingPlayer.isPresent()) {
            gameInvolvingPlayer.get().updatePeerIfOlder(username, peer);
            var ret = gameInvolvingPlayer.get().process(sugarMessage, peer);

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

    private Optional<GameController> findGameInvolvingPlayer(String player) {
        return this.games
                .stream()
                .filter(gameController -> gameController.containsPlayer(player))
                .findFirst();
    }

    @SugarMessageHandler
    public void chatMsg(SugarMessage message, Peer peer) {
        var msg = (ChatMsg) message;
        var username = AuthController.getUsernameFromJWT(msg.jwt);
        var gameController = findGameInvolvingPlayer(username);
        @NotNull var textMessage = msg.message;

        if(gameController.isPresent()) {
            // Send the message to all the players in the room
            if(msg.to.equals("all")) {
                this.gameLogicMulticast(gameController.get(), new ChatMsg(username, "all", textMessage));
            // Send the message only to my teammates
            } else if(msg.to.equals("team")) {
                gameController.get()
                        .getTeamPeers(username)
                        .forEach(pr -> this.send(new ChatMsg(username, "team", textMessage), pr));
            // Send the message to a player
            } else {
                // Getting the peer from the username given, for sending the message
                var peerFromPlayer = gameController.get()
                        .getPeerFromPlayer(msg.to);

                // Check if the user is present in the game or not
                peerFromPlayer.ifPresentOrElse(
                        pr -> {
                            this.send(new ChatMsg(username, msg.to, textMessage), pr);
                            this.send(new ChatMsg(username, username, textMessage), peer);
                        },
                        () ->   this.send(new KOMsg("No user found in this game with the username given"), peer))
                ;
            }
        // The game is not present, the user have probably provided a fake JWT token
        } else {
            this.send(new KOMsg("No game found"), peer);
        }
    }

    private void send(SugarMessage message, Peer peer) {
        try {
            this.server.send(message, peer);
        } catch (IOException ignored) {}
    }


    // Handling messages from lower layers
//    @SugarMessageFromLowerLayersHandler
//    public void gameOverMsg(SugarMessage message) { // From CommunicationController
//        GameOverMsg msg = (GameOverMsg) message;
//
//        String aPeerFromThisGame = null;
//
//        // Notify clients
//        for(var peer : msg.peerToIsWinner.keySet()) {
//            aPeerFromThisGame = peer;
//            try {
//                this.server.send(new NotifyGameOverMsg(
//                        msg.peerToIsWinner.get(peer) ? ReturnMessage.YOU_WIN.text : ReturnMessage.YOU_LOSE.text
//                ).serialize(), );
//            } catch (IOException ignored) { }
//        }
//
//        // Close game
//        var game = this.findGameInvolvingPeer(aPeerFromThisGame);
//        game.ifPresent(this.games::remove);
//    }

    private void gameLogicMulticast(GameController gameController, SugarMessage message) {
        var players = gameController.getPlayers();
        for(var player: players) {
            try {
                this.server.send(message, player.associatedPeer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SugarMessageFromLowerLayersHandler
    public void okAndUpdateMsg(SugarMessage message, Peer receiver) {
        var msg = (OKAndUpdateMsg) message;

        try {
            this.server.send(msg.okMsg.serialize(), receiver.peerSocket);

            var gameInvolvingReceiver = findGameInvolvingPeer(receiver);
            gameInvolvingReceiver.ifPresent(gameController -> this.gameLogicMulticast(gameController, msg.updateClientMsg));
        } catch (IOException ignored) {} {}
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
