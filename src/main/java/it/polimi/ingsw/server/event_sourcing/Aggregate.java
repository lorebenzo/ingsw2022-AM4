package it.polimi.ingsw.server.event_sourcing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public abstract class Aggregate {
     public UUID id = UUID.randomUUID();
     public final Date created = new Date();
     public static EventStore repository;

     static {
          try {
               repository = new EventStore();
          } catch (ClassNotFoundException | SQLException e) {
               e.printStackTrace();
          }
     }

     public Aggregate() {

     }

     public Aggregate(UUID uuid) {
          this.id = uuid;
     }

     public static Aggregate loadFromUuid(UUID uuid) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
          String aggregateType = repository.getAggregateType(uuid);

          Class<?> aggregateClass = Class.forName(aggregateType);
          Constructor<?> constructor = aggregateClass.getConstructor(UUID.class);
          Aggregate aggregate = (Aggregate) constructor.newInstance(uuid);

          aggregate.getCurrentState();

          return aggregate;
     }

     public void getCurrentState() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, ClassNotFoundException {
          for(Event event: repository.getEvents(this.id)) {
               Method handler = this.getClass().getMethod(event.handlerName, event.getClass());
               handler.invoke(this, event);
          }
     }

     // TODO: IMPLEMENTARE CREATE SNAPSHOT
     public void createSnapshot() {};
}
