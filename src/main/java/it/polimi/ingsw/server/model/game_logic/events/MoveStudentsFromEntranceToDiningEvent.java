package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.UUID;

public class MoveStudentsFromEntranceToDiningEvent extends Event {
    public final Color student;

    public MoveStudentsFromEntranceToDiningEvent(UUID parentEvent, Color student) {
        super("moveStudentFromEntranceToDiningHandler", parentEvent);
        this.student = student;
    }
}
