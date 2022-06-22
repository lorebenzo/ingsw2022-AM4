package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.ActionPhaseSubTurn;

import java.util.UUID;

public class SetActionPhaseSubTurnEvent extends Event {
    public final ActionPhaseSubTurn actionPhaseSubTurn;

    public SetActionPhaseSubTurnEvent(UUID parentEvent, ActionPhaseSubTurn actionPhaseSubTurn) {
        super("setActionPhaseSubTurnHandler", parentEvent);
        this.actionPhaseSubTurn = actionPhaseSubTurn;
    }
}
