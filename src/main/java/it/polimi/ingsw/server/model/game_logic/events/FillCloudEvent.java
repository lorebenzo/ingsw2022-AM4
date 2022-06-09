package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class FillCloudEvent extends Event {
    public final int cloudIndex;

    public FillCloudEvent(UUID parentEvent, int cloudIndex) {
        super("fillCloudHandler", parentEvent);
        this.cloudIndex = cloudIndex;
    }
}
