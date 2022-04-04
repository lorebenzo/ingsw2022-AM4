package it.polimi.ingsw.server.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.game_logic.StudentFactory;

import java.util.UUID;

public class CreateStudentFactoryEvent extends Event {

    private final StudentFactory studentFactory;

    public CreateStudentFactoryEvent(StudentFactory studentFactory, UUID parentEventID) {
        super("createStudentFactoryHandler", parentEventID);
        this.studentFactory = studentFactory;
    }

    public StudentFactory getStudentFactory() {
        return studentFactory;
    }
}
