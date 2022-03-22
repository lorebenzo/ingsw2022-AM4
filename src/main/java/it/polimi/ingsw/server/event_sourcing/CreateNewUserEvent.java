package it.polimi.ingsw.server.event_sourcing;

public class CreateNewUserEvent extends Event {
    private final String surname;
    private final String name;

    public CreateNewUserEvent(String name, String surname) {
        super("createNewUserHandler");
        this.surname = surname;
        this.name = name;
    }

    public String getSurname() {
        return new String(surname);
    }

    public String getName() {
        return new String(name);
    }
}
