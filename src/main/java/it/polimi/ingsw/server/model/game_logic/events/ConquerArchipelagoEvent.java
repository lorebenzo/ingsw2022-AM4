package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class ConquerArchipelagoEvent extends Event {
    public final int schoolBoardID;

    public ConquerArchipelagoEvent(UUID parentEvent, int schoolBoardID) {
        super("conquerArchipelagoHandler", parentEvent);
        this.schoolBoardID = schoolBoardID;
    }
}
