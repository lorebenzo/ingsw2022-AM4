package it.polimi.ingsw.server.server_logic;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.games_manager.GamesManager;

import java.io.IOException;

public class GameServer extends SugarServer {

    private final GamesManager gamesManager = new GamesManager(this);

    public GameServer() throws IOException {
        super();
    }

    @Override
    protected void onPeerConnect(Peer peer) {}

    @Override
    protected void onPeerDisconnect(Peer peer) {}

    @Override
    protected void onPeerMessage(Peer peer, SugarMessage message) {
        var response = gamesManager.process(message, peer);
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
