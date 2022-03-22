package it.polimi.ingsw.server.event_sourcing;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class Event implements Serializable {
    public final UUID id = UUID.randomUUID();
    public final Date created = new Date();
    public String handlerName;

    public Event(String handlerName) {
        this.handlerName = handlerName;
    }
}

