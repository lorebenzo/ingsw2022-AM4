package it.polimi.ingsw.server.repository;

import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.communication.sugar_framework.SerDes;
import it.polimi.ingsw.server.event_sourcing.Aggregate;
import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.event_sourcing.EventsMapper;
import it.polimi.ingsw.server.model.game_logic.GameState;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.sql.*;
import java.util.*;

public class EventsRepository {
    public static Connection c;
    private static EventsRepository instance = null;
    private static final Dotenv dotenv = Dotenv.configure().load();


    private EventsRepository() throws DBNotFoundException {
        try {
            c = DriverManager
                    .getConnection(
                            "jdbc:postgresql://" +
                                    dotenv.get("DB_HOST") + ":" +
                                    dotenv.get("DB_PORT") + "/" +
                                    dotenv.get("DB_NAME"),
                            dotenv.get("DB_USER"),
                            dotenv.get("DB_PASSWORD")
                    );
        } catch (Exception ignored) {
            throw new DBNotFoundException();
        }
    }

    public static EventsRepository getInstance() throws DBNotFoundException {
        if(instance == null)
            try {
                instance = new EventsRepository();
            } catch (DBNotFoundException e) {
                System.err.println("DB not connected.");
            }
        return instance;
    }

    public void rollback(UUID aggregateUUID, int aggregateVersion) throws DBQueryException {
        if(instance != null)
            try {
                Statement stmt = c.createStatement();

                String sql = "delete\n" +
                            "from event.events\n" +
                            "where aggregate_id = '" + aggregateUUID + "'\n" +
                            "and aggregate_version >= " + aggregateVersion;

                stmt.executeUpdate(sql);
                stmt.close();
            } catch (SQLException e) {
                throw new DBQueryException();
            }
    }

    public void storeAggregate(UUID uuid, String aggregateClass) throws SQLException {
        if(instance != null) {
            Statement stmt = c.createStatement();
            String sql = "INSERT INTO event.aggregates (id, type)\n" + "VALUES ('" + uuid + "', '"+ 1 +"');";
            stmt.executeUpdate(sql);
            stmt.close();
        }
    }

    public void generateSnapshot(Aggregate aggregate) throws DBQueryException {
        if(instance != null)
            try {
                Statement stmt = c.createStatement();

                String sql = "INSERT INTO event.snapshots (aggregate_id, version, snap)\n"
                        + "VALUES ('"
                        + aggregate.id + "', '"
                        + aggregate.version + "', '"
                        + SerDes.serialize(aggregate) + "');";

                stmt.executeUpdate(sql);
                stmt.close();
            } catch (SQLException e) {
                throw new DBQueryException();
            }
    }

    public String getAggregateType(UUID uuid) throws DBQueryException {
        String type = null;
        if(instance != null) {
            try {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * FROM event.aggregates INNER JOIN event.aggregate_type ON type=aggregate_id "
                                                    + "WHERE id = '" + uuid + "'");
    
                rs.next();
                type = rs.getString("aggregate_name");
    
                rs.close();
                stmt.close();
            } catch(SQLException ignored) {
                ignored.printStackTrace();
                throw new DBQueryException();
            }
        }
        return type;
    }

    public void addEvent(UUID aggregateUuid, Event event, int aggregateVersion) throws DBQueryException {
        if(instance != null) {
            try {
                Statement stmt = c.createStatement();
                String sql = "INSERT INTO event.events (ID, event, aggregate_id, event_class, event_parent_uuid, aggregate_version)\n"
                        + "VALUES ('"
                        + event.id + "', '"
                        + SerDes.serialize(event) + "', '"
                        + aggregateUuid + "', '"
                        + EventsMapper.getKeyByValue(event.getClass()) + "', '"
                        + event.parentEvent + "', '"
                        + aggregateVersion + "');";


                stmt.executeUpdate(sql);
                stmt.close();
            } catch (SQLException ignored) {
                throw new DBQueryException();
            }
        }
    }

    public Collection<Event> getEvents(UUID uuid, int aggregateVersion) throws DBQueryException {
        if(instance != null) {
            Collection<Event> events = new Stack<>();

            try {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM event.events "
                        + "WHERE aggregate_id = '"
                        + uuid + "' " +
                        "and aggregate_version > " + aggregateVersion + ";");

                while (rs.next()) {
                    String event = rs.getString("event");
                    Integer eventClass = rs.getInt("event_class");
                    Event eventParsed = (Event) SerDes.deserialize(event, EventsMapper.getFromId(eventClass));
                    events.add(eventParsed);
                }
                rs.close();
                stmt.close();
            } catch (SQLException ignored) {
                throw new DBQueryException();
            }
            return events;
        } else {
            return new Stack<>();
        }
    }

    public Aggregate getSnapshot(UUID aggregateID) throws DBQueryException {
        if(instance != null) {
            try {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("select * " +
                        "from event.snapshots " +
                        "where aggregate_id = '" + aggregateID + "' " +
                        "order by version desc " +
                        "fetch first 1 rows only;");

                Aggregate aggregateParsed = null;
                int version = 0;
                while (rs.next()) {
                    String aggregate = rs.getString("snap");
                    version = rs.getInt("version");

                    aggregateParsed = (GameState) SerDes.deserialize(aggregate, GameState.class);
                }
                rs.close();
                stmt.close();
                return aggregateParsed;
            } catch (SQLException ignored) {
                throw new DBQueryException();
            }
        }

        return null;
    }
}