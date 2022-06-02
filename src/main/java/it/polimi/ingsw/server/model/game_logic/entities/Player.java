package it.polimi.ingsw.server.model.game_logic.entities;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import org.jetbrains.annotations.NotNull;

public class Player implements Comparable<Player> {
    public final Peer associatedPeer;
    public final String username;

    public Player(Peer associatedPeer, String username) {
        this.associatedPeer = associatedPeer;
        this.username = username;
    }

    @Override
    public int compareTo(@NotNull Player player) {
        if (this.username.equals(player.username)) {
            return 0;
        }
        return -1;
    }
}
