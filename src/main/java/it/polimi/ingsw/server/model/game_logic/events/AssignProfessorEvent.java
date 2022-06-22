package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.UUID;

public class AssignProfessorEvent extends Event {
    public final Color professor;
    public AssignProfessorEvent(UUID parentEvent, Color professor) {
        super("assignProfessorHandler", parentEvent);

        this.professor = professor;
    }
}
