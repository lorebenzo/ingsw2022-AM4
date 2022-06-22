package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.List;
import java.util.UUID;

public class SetRoundOrderEvent extends Event {
    public final List<Integer> roundOrder;

    public SetRoundOrderEvent(UUID parentEvent, List<Integer> roundOrder) {
        super("setRoundOrderHandler", parentEvent);
        this.roundOrder = roundOrder;
    }
}
