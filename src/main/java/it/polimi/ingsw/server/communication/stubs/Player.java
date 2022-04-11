package it.polimi.ingsw.server.communication.stubs;

import java.util.Objects;
import java.util.UUID;

public class Player {
    public final UUID playerID;

    public Player(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerID, player.playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerID);
    }
}
