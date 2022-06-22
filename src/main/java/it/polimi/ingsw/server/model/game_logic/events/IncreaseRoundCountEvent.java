package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class IncreaseRoundCountEvent extends Event {

    public IncreaseRoundCountEvent(UUID parentEvent) {
        super("increaseRoundCountHandler", parentEvent);
    }
}
