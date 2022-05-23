package it.polimi.ingsw.server.repository;

import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.sql.*;
import java.util.UUID;

public class UsersRepository {
    public Connection c;
    private static UsersRepository instance;
    private static final Dotenv dotenv = Dotenv.configure().load();

    /**
     * Singleton, private constructor
     * @throws DBNotFoundException if there is a problem with the connection to the DB
     */
    private UsersRepository() throws DBNotFoundException {
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

    public static UsersRepository getInstance() {
        if(instance == null)
            try {
                instance = new UsersRepository();
            } catch (Exception ignored) {}
        return instance;
    }

    /**
     * Insert the user in the DB
     * @param username of the user
     * @param password of the user
     * @throws DBQueryException if there is a problem with the DB i.e. username already present
     */
    public void signUpUser(String username, String password) throws DBQueryException {
        try {
            Statement stmt = c.createStatement();

            String sql = "INSERT INTO users.users (username, password)\n"
                    + "VALUES ('" + username + "', '"+ password +"');";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            throw new DBQueryException();
        }
    }

    /**
     * Get the hashed password of the user
     * @param username of the user
     * @return the hashed password
     * @throws DBQueryException if the is no user with this username, or there is a problem with the DB
     */
    public String getUserHashedPassword(String username) throws DBQueryException {
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * from users.users where username = '" + username + "'" );

            rs.next();
            var hashedPwd = rs.getString("password");

            stmt.close();
            return hashedPwd.trim();
        } catch(Exception e) {
            throw new DBQueryException();
        }
    }

    /**
     * Save the map from username to the gameUUID and the schoolBoardID
     * @param gameUUID of the game
     * @param username of the user
     * @param schoolBoardID of the user
     */
    public void saveUserSchoolBardMap(UUID gameUUID, String username, int schoolBoardID) {
        try {
            Statement stmt = c.createStatement();
            String sql = "INSERT INTO users.user_game (username, \"gameUUID\", \"schoolBoardID\")\n"
                    + "VALUES ('"
                    + username + "', '"
                    + gameUUID +"', '"
                    + schoolBoardID + "');";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
