package it.polimi.ingsw.server.repository;

import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.server.repository.exceptions.DBNotFoundException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.sql.*;

public class UsersRepository {
    public Connection c;
    private static UsersRepository instance;
    private static final Dotenv dotenv = Dotenv.configure().load();

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
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getUserHashedPassword(String username) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * from users.users where username = '" + username + "'" );

        rs.next();
        var hashedPwd = rs.getString("password");
        stmt.close();
        return hashedPwd.trim();
    }
}
