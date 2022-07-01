package it.polimi.ingsw.server.event_sourcing.exceptions;

public class EventSourcingException extends Exception{
    public EventSourcingException(String message, Throwable cause) {
        super(message, cause);
    }
}
