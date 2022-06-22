package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class GrabStudentsFromCloudEvent extends Event {
    public final int cloudIndex;

    public GrabStudentsFromCloudEvent(UUID parentEvent, int cloudIndex) {
        super("grabStudentsFromCloudHandler", parentEvent);
        this.cloudIndex = cloudIndex;
    }
}
