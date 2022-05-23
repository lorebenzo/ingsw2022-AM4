package it.polimi.ingsw.server.model.game_logic.entities;

import it.polimi.ingsw.communication.sugar_framework.Peer;

public class Player {
    public final Peer associatedPeer;
    public final String username;

    public Player(Peer associatedPeer, String username) {
        this.associatedPeer = associatedPeer;
        this.username = username;
    }
}
