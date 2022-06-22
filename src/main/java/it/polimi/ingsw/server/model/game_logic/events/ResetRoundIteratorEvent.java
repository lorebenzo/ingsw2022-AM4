package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class ResetRoundIteratorEvent extends Event {
    public ResetRoundIteratorEvent(UUID parentEvent) {
        super("resetRoundIteratorHandler", parentEvent);
    }
}
