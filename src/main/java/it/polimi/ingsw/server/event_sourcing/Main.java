package it.polimi.ingsw.server.event_sourcing;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException, ClassNotFoundException, InstantiationException {
        // change name with marco, 12345

        User user = new User("Lorenzo", "Benzoni");
        user.changeName("marco123");
//        user.changeName("franco");
//        user.changeName("lorenzo");
//        user.changeName("luca");
//        user.changeName("marco");

        User user2 = User.loadUser(user.id);

        user2 = (User) User.loadFromUuid(user.id);

//        User user2 = new User(user.id);
//
//        user2.getCurrentState();

        System.out.println(user2.name);
    }
}
