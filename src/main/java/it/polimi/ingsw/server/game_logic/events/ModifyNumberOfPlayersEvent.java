package it.polimi.ingsw.server.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class ModifyNumberOfPlayersEvent extends Event {
    public final int players;
    public ModifyNumberOfPlayersEvent(UUID parentEvent, int players) {
        super("modifyPlayersHandler", parentEvent);
        this.players = players;
    }

    public int getPlayers() {
        return players;
    }
}
