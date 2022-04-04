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
               repository = EventStore.getInstance();
          } catch (SQLException | ClassNotFoundException e) {
               e.printStackTrace();
          }
     }

     public int version = 0;

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

          aggregate = aggregate.getCurrentState();

          return aggregate;
     }

     public Aggregate getCurrentState() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, ClassNotFoundException {
          Aggregate lastSnap = repository.getSnapshot(this.id);
          if(lastSnap == null) {
               lastSnap = this;
          }
          for(Event event: repository.getEvents(this.id, this.version)) {
               Method handler = lastSnap.getClass().getMethod(event.handlerName, event.getClass());
               handler.invoke(lastSnap, event);
          }
          return lastSnap;
     }

     public void apply(Event event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
          Method handler = this.getClass().getMethod(event.handlerName, event.getClass());
          handler.invoke(this, event);
     }

     public void createSnapshot() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
          this.getCurrentState();
          repository.generateSnapshot(this);
     };
}
