package it.polimi.ingsw.server.event_sourcing;

import java.util.UUID;

public class ChangeNameEvent extends Event {
    private final String name;

    public ChangeNameEvent(String name) {
        super("changeNameHandler");
        this.name = name;
    }

    public String getName() {
        return new String(name);
    }
}
