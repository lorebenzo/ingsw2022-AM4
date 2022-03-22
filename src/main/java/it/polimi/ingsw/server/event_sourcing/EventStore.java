package it.polimi.ingsw.server.event_sourcing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class EventStore implements Serializable {
    public List<Event> store = new LinkedList<>();
    Connection c = null;

    public EventStore() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        this.c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/db",
                        "user", "password");


    }

    public void storeAggregate(UUID uuid, String aggregateClass) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO aggregates (id, type)\n" + "VALUES ('" + uuid + "', '"+ aggregateClass +"');";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public String getAggregateType(UUID uuid) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM aggregates WHERE id = '" + uuid + "';" );

        rs.next();
        String type = rs.getString("type");

        rs.close();
        stmt.close();
        return type;
    }

    public void addEvent(UUID aggregateUuid, Event event) throws SQLException, ClassNotFoundException {
        this.store.add(event);

        Statement stmt = c.createStatement();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String sql = "INSERT INTO events (ID, event, aggregate_id, event_class)\n" + "VALUES ('" + event.id + "', '"+ gson.toJson(event) +"', '" + aggregateUuid + "', '" + event.getClass().getName() +"');";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public Collection<Event> getEvents(UUID uuid) throws SQLException, ClassNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Collection<Event> events = new Stack<>();

        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM events WHERE aggregate_id = '" + uuid + "';" );

        while ( rs.next() ) {
            String event = rs.getString("event");
            String eventClass = rs.getString("event_class");
            Event eventParsed = (Event) gson.fromJson(event, Class.forName(eventClass));
            events.add(eventParsed);
        }
        rs.close();
        stmt.close();
        return events;
    }
}
