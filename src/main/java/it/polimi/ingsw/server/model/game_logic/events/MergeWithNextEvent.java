package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class MergeWithNextEvent extends Event {
    public MergeWithNextEvent(UUID parentEvent) {
        super("mergeWithNextHandler", parentEvent);
    }
}
