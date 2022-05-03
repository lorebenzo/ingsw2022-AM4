package it.polimi.ingsw.server.server_logic;

import it.polimi.ingsw.communication.sugar_framework.Peer;

public class Player {
    public final Peer associatedPeer;

    public Player(Peer associatedPeer) {
        this.associatedPeer = associatedPeer;
    }
}
