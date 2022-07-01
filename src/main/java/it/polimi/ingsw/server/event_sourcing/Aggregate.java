package it.polimi.ingsw.server.event_sourcing;

import it.polimi.ingsw.server.event_sourcing.exceptions.EventSourcingException;
import it.polimi.ingsw.server.repository.EventsRepository;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

public abstract class Aggregate {
    public UUID id = UUID.randomUUID();
    public final Date created = new Date();
    public static EventsRepository repository;
    public int version = 0;

    static {
        try {
            repository = EventsRepository.getInstance();
        } catch (DBNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Aggregate() { }

    public Aggregate(UUID uuid) {
        this.id = uuid;
    }

    /**
     * Returns the updated aggregate based on the UUID
     * @param uuid of the Aggregate to recover
     * @throws EventSourcingException if there is some problem with the handling of the events
     */
    public static Aggregate loadFromUuid(UUID uuid) throws EventSourcingException {
        Aggregate aggregate;
        try {
            String aggregateType = repository.getAggregateType(uuid);

            Class<?> aggregateClass = Class.forName(aggregateType);
            Constructor<?> constructor = aggregateClass.getConstructor(UUID.class);
            aggregate = (Aggregate) constructor.newInstance(uuid);

            aggregate = aggregate.getCurrentState();
        } catch (Exception e) {
            throw new EventSourcingException(e.getMessage(), e);
        }

        return aggregate;
    }

    /**
     * Restore the aggregate and return the most updated version of the aggregate
     * For doing so, it reconstructs the aggregate from the snapshot and applies the stack of events
     * @return the updated aggregate
     * @throws EventSourcingException if there is a problem with the events handlers
     * @throws DBQueryException if there is a problem with the DB
     */
    public Aggregate getCurrentState() throws EventSourcingException, DBQueryException {
        Aggregate lastSnap = null;

        try {
            lastSnap = repository.getSnapshot(this.id);
        } catch (DBQueryException e) { throw new EventSourcingException(e.getMessage(), e); }

        // There is no snapshot, recover from the current version
        if(lastSnap == null) {
            lastSnap = this;
        }

        // For every event, apply it
        for(Event event: repository.getEvents(this.id, lastSnap.version)) {
            lastSnap.apply(event);
        }
        return lastSnap;
    }

    /**
     * Applies an event, given the Event to apply
     * This method will call using reflection the appropriate handler that manage the event
     * @param event to apply
     * @throws EventSourcingException if there is some problem finding the appropriate handler
     */
    public void apply(Event event) throws EventSourcingException {
        try {
            // Invoke the correct handler based on the event.handlerName
            Method handler = this.getClass().getMethod(event.handlerName, event.getClass());
            handler.invoke(this, event);
        } catch (Exception e) {
            throw new EventSourcingException(e.getMessage(), e);
        }
    }

    /**
     * Generates the snapshot and saves the Aggregate updated information to the repository
     */
    public void createSnapshot() {
        try {
            this.getCurrentState();
            if(repository!= null)
                repository.generateSnapshot(this);
        } catch (Exception e) {
            System.err.println("Error while creating the snapshot, please check if the DB is connected");
        }
    }

    /**
     * Recreates the aggregate based on the last snapshot, deletes all the events vit aggregate
     * version > snapshot version
     * @return the Aggregate Snapshot
     */
    public Aggregate rollback() {
        if (repository != null) {
            try {
                // Get the old snapshot version
                Aggregate lastSnapVersion = repository.getSnapshot(this.id);
                // Rollback in the repository, ideally it removes the events stack
                repository.rollback(this.id, lastSnapVersion.version);
                // Recover the current states without the removed events
                return this.getCurrentState();
            } catch (EventSourcingException | DBQueryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}