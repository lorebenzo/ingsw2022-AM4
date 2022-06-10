package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class MoveMotherNatureNStepsClockwiseEvent extends Event {
    public final int steps;

    public MoveMotherNatureNStepsClockwiseEvent(UUID parentEvent, int steps) {
        super("moveMotherNatureNStepsClockwiseHandler", parentEvent);
        this.steps = steps;
    }
}
