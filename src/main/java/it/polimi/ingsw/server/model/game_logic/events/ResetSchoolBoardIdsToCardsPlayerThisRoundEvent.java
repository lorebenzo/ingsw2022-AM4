package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class ResetSchoolBoardIdsToCardsPlayerThisRoundEvent extends Event {
    public ResetSchoolBoardIdsToCardsPlayerThisRoundEvent(UUID parentEvent) {
        super("resetSchoolBoardIdsToCardsPlayerThisRoundHandler", parentEvent);
    }
}
