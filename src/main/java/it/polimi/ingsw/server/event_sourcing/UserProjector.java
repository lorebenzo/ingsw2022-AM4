package it.polimi.ingsw.server.event_sourcing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class UserProjector {

    private final EventStore eventStore;

    public UserProjector(EventStore eventStore) {
        this.eventStore = eventStore;
    }

}
