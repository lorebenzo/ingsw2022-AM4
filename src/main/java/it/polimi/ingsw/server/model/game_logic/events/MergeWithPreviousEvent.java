package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class MergeWithPreviousEvent extends Event {
    public MergeWithPreviousEvent(UUID parentEvent) {
        super("mergeWithPreviousHandler", parentEvent);
    }
}
