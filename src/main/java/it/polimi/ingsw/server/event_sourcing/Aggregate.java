package it.polimi.ingsw.server.event_sourcing;

import it.polimi.ingsw.server.repository.EventsRepository;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
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

    public static Aggregate loadFromUuid(UUID uuid) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, DBQueryException {
        String aggregateType = repository.getAggregateType(uuid);

        Class<?> aggregateClass = Class.forName(aggregateType);
        Constructor<?> constructor = aggregateClass.getConstructor(UUID.class);
        Aggregate aggregate = (Aggregate) constructor.newInstance(uuid);

        aggregate = aggregate.getCurrentState();

        return aggregate;
    }

    public Aggregate getCurrentState() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, DBQueryException {
        Aggregate lastSnap = repository.getSnapshot(this.id);
        if(lastSnap == null) {
            lastSnap = this;
        }
        for(Event event: repository.getEvents(this.id, lastSnap.version)) {
            lastSnap.apply(event);
        }
        return lastSnap;
    }

    public void apply(Event event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method handler = this.getClass().getMethod(event.handlerName, event.getClass());
        handler.invoke(this, event);
    }

    public void createSnapshot() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, DBQueryException, SQLException {
        this.getCurrentState();
        repository.generateSnapshot(this);
    }

    public Aggregate rollback() {
        try {
            Aggregate lastSnapVersion = repository.getSnapshot(this.id);
            repository.rollback(this.id, lastSnapVersion.version);
            return this.getCurrentState();
        } catch (SQLException | DBQueryException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}