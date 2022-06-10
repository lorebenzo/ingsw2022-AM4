package it.polimi.ingsw.server.server_logic;

import io.jsonwebtoken.Jwts;
import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.games_manager.messages.PeerDisconnectedFromGameMsg;

import java.io.IOException;

public class GameServer extends SugarServer {
    private final AuthController authController = new AuthController(this);

    public GameServer() throws IOException {
        super();
        this.authController.gamesManager.recoverCurrentGames();
    }

    @Override
    protected void onPeerConnect(Peer peer) {}

    @Override
    protected void onPeerDisconnect(Peer peer) {
        var message = new PeerDisconnectedFromGameMsg(AuthController.createFakeJWT());
        var response = authController.process(message, peer);
        if(response != null) {
            try {
                this.send(response, peer);
            } catch (IOException e) {
                this.log("Could not send response message to peer " + peer + "\nMessage received from the server was: " + message.serialize());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPeerMessage(Peer peer, SugarMessage message) {
        var response = authController.process(message, peer);
        if(response != null) {
            try {
                this.send(response, peer);
            } catch (IOException e) {
                this.log("Could not send response message to peer " + peer + "\nMessage received from the server was: " + message.serialize());
                e.printStackTrace();
            }
        }
    }
}
