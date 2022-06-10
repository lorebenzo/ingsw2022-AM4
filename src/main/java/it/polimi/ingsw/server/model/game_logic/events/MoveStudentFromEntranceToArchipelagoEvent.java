package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.List;
import java.util.UUID;

public class MoveStudentFromEntranceToArchipelagoEvent extends Event {
    public final Color student;
    public final List<Integer> archipelagoIslandCodes;

    public MoveStudentFromEntranceToArchipelagoEvent(UUID parentEvent, Color student, List<Integer> archipelagoIslandCodes) {
        super("moveStudentFromEntranceToArchipelagoHandler", parentEvent);
        this.student = student;
        this.archipelagoIslandCodes = archipelagoIslandCodes;
    }
}
