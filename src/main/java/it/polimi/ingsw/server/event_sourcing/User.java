package it.polimi.ingsw.server.event_sourcing;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.UUID;

public class User extends Aggregate {

    public String name;
    public String surname;

    public User(String name, String surname) throws SQLException, ClassNotFoundException {
        super();
        repository.storeAggregate(this.id, this.getClass().getName());
        repository.addEvent(this.id, new CreateNewUserEvent(name, surname));
    }

    public User(UUID id) {
        super(id);
    }

    public User() throws SQLException {
        super();
        repository.storeAggregate(this.id, this.getClass().getName());
    }

    public static User loadUser (UUID uuid) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException, ClassNotFoundException {

        User user = new User(uuid);
        user.getCurrentState();

        return user;
    }

    public void changeName(String name) throws SQLException, ClassNotFoundException {
        repository.addEvent(this.id, new ChangeNameEvent(name));
    }

    public void changeNameHandler(ChangeNameEvent event) {
        this.name = event.getName();

        ///

        ///

        ///

        ///
    }

    public void createNewUserHandler(CreateNewUserEvent event) {
        this.name = event.getName();
        this.surname = event.getSurname();
    }
}
