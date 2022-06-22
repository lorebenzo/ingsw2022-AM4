package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.UUID;

public class SetCurrentPhaseEvent extends Event {
    public final Phase phase;
    public SetCurrentPhaseEvent(UUID parentEvent, Phase phase) {
        super("setCurrentPhaseHandler", parentEvent);
        this.phase = phase;
    }
}
