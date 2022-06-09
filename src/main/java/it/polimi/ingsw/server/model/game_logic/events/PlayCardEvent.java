package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.Card;

import java.util.UUID;

public class PlayCardEvent extends Event {
    public final Card card;

    public PlayCardEvent(UUID parentEvent, Card card) {
        super("playCardHandler", parentEvent);
        this.card = card;
    }
}
