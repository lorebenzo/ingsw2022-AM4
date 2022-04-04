package it.polimi.ingsw.server.event_sourcing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.server.game_logic.GameState;
import java.sql.*;
import java.util.*;

public class EventStore {
//    private final List<Event> store = new LinkedList<>();
    public static Connection c;
    private static Gson gson;
    private static EventStore instance = null;

    private EventStore() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        c = DriverManager
                .getConnection(
                        "jdbc:postgresql://localhost:5432/game",
                        "user", "password"
                );
    }

    public static EventStore getInstance() throws SQLException, ClassNotFoundException {
        if(instance == null)
            instance = new EventStore();
        return instance;
    }

    public void storeAggregate(UUID uuid, String aggregateClass) throws SQLException {
        Statement stmt = c.createStatement();
        // todo: rendere genrico
        String sql = "INSERT INTO event.aggregates (id, type)\n" + "VALUES ('" + uuid + "', '"+ 1 +"');";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void generateSnapshot(Aggregate aggregate) throws SQLException {
        Statement stmt = c.createStatement();

        String sql = "INSERT INTO event.snapshots (aggregate_id, version, snap)\n" + "VALUES ('" + aggregate.id + "', '"+ aggregate.version +"', '" + gson.toJson(aggregate) + "');";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public String getAggregateType(UUID uuid) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM event.aggregates INNER JOIN event.aggregate_type ON type=aggregate_id WHERE id = '" + uuid + "'");

        rs.next();
        String type = rs.getString("aggregate_name");

        rs.close();
        stmt.close();
        return type;
    }

    public void addEvent(UUID aggregateUuid, Event event, int aggregateVersion) throws SQLException, ClassNotFoundException {
//        store.add(event);
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO event.events (ID, event, aggregate_id, event_class, event_parent_uuid, aggregate_version)\n" + "VALUES ('" + event.id + "', '"+ gson.toJson(event) +"', '" + aggregateUuid + "', '" + EventsMapper.getKeyByValue(event.getClass()) +"', '" + event.parentEvent + "', '" + aggregateVersion + "');";

        stmt.executeUpdate(sql);
        stmt.close();
    }

    public Collection<Event> getEvents(UUID uuid, int aggregateVersion) throws SQLException {

        Collection<Event> events = new Stack<>();

        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM event.events WHERE aggregate_id = '" + uuid + "' and aggregate_version > " + aggregateVersion + ";" );

        while ( rs.next() ) {
            String event = rs.getString("event");
            Integer eventClass = rs.getInt("event_class");
            Event eventParsed = (Event) gson.fromJson(event, EventsMapper.getFromId(eventClass));
            events.add(eventParsed);
        }
        rs.close();
        stmt.close();
        return events;
    }

    public Aggregate getSnapshot(UUID aggregateID) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("select * " +
                        "from event.snapshots " +
                        "where aggregate_id = '" + aggregateID + "' " +
                        "order by version desc " +
                        "fetch first 1 rows only;" );

        Aggregate aggregateParsed = null;
        while ( rs.next() ) {
            String aggregate = rs.getString("snap");
            // TODO: DA MODIFICARE, RENDERE GENERICO
            aggregateParsed = gson.fromJson(aggregate, GameState.class);
        }
        rs.close();
        stmt.close();
        return aggregateParsed;
    }
}
