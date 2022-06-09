package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class InitGameStateEvent extends Event {
    public final int numberOfPlayers;

    public InitGameStateEvent(UUID parentEvent, int numberOfPlayers) {
        super("initGameState", parentEvent);
        this.numberOfPlayers = numberOfPlayers;
    }
}
