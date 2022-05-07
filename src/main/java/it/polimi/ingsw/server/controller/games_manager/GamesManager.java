package it.polimi.ingsw.server.controller.games_manager;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.controller.game_controller.GameController;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinGameMsg;

import java.util.LinkedList;
import java.util.List;

public class GamesManager extends SugarMessageProcessor {
    private final List<GameController> games = new LinkedList<>();
    private final SugarServer server;

    public GamesManager(SugarServer server) {
        this.server = server;
    }

    @SugarMessageHandler
    public SugarMessage joinGameMsg(SugarMessage message, Peer peer) {
        var msg = (JoinGameMsg) message;

        // Check if a game with that roomId already exists
        var chosenGame = this.games.stream().filter(game -> game.roomId.equals(msg.roomId)).findFirst();

        if(chosenGame.isPresent()) {
            // Add the player to the game
            chosenGame.get().addPlayer(new Player(peer));
        } else {
            // Create a new game and add the player
            server.createRoom(msg.roomId);
            synchronized (this.games) {
                this.games.add(new GameController(msg.roomId));
            }
        }

        return null;
    }

    // @SugarMessageHandler
    // public SugarMessage base(SugarMessage sugarMessage, Peer peer) {
    //    games.forEach(game-> game.process(sugarMessage, peer));
    // }
}
