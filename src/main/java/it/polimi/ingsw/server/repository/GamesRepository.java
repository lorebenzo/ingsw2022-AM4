package it.polimi.ingsw.server.repository;

import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.server.event_sourcing.Aggregate;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import org.javatuples.Pair;

import java.sql.*;
import java.util.*;

public class GamesRepository {
    private final Connection c;
    private static GamesRepository instance;
    private static final Dotenv dotenv = Dotenv.configure().load();

    /**
     * Singleton, private constructor
     * @throws DBNotFoundException if there is a problem with the connection to the DB
     */
    private GamesRepository() throws DBNotFoundException {
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

    public static GamesRepository getInstance() {
        if(instance == null)
            try {
                instance = new GamesRepository();
            } catch (Exception ignored) {}
        return instance;
    }

    /**
     * Save the map from username to the gameUUID and the schoolBoardID
     * @param gameUUID of the game
     * @param username of the user
     * @param schoolBoardID of the user
     */
    public void saveUserSchoolBoardMap(UUID gameUUID, String username, int schoolBoardID) {
        try {
            Statement stmt = c.createStatement();
            String sql = "INSERT INTO users.current_games (username, \"gameUUID\", \"schoolBoardID\", \"expert\")\n"
                    + "VALUES ('"
                    + username + "', '"
                    + gameUUID +"', '"
                    + schoolBoardID + "', '"
                    + "false" + "');";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromCurrentGames(UUID gameUUID) {
        try {
            Statement stmt = c.createStatement();
            String sql = "delete\n" +
                        "from users.current_games\n" +
                        "where \"gameUUID\" = '" + gameUUID + "'";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return map (GameUUID, expertMode) -> List(username, schoolBoardID)
     * @return a map that contains games, usernames and schoolBoardIDs
     */
    public Map<Pair<String, Boolean>, List<Pair<String, Integer>>> getCurrentGames() {
        var currentGames = new HashMap<Pair<String, Boolean>, List<Pair<String, Integer>>>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("select *\n" +
                                                 "from users.current_games \n" +
                                                "where expert = false");

            while ( rs.next() ) {
                String gameUUID = rs.getString("gameUUID");
                String username = rs.getString("username");
                Integer schoolBoardID = rs.getInt("schoolBoardID");
                Boolean expertMode = rs.getBoolean("expert");

                var usersInGame = currentGames.get(Pair.with(gameUUID, expertMode));
                if(usersInGame == null)
                    usersInGame = new LinkedList<>();
                usersInGame.add(new Pair<>(username, schoolBoardID));
                currentGames.put(Pair.with(gameUUID, expertMode), usersInGame);
            }
            rs.close();
            stmt.close();
        } catch(Exception e) {e.printStackTrace();};
        return currentGames;
    }
}
