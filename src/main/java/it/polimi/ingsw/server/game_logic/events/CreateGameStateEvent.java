package it.polimi.ingsw.server.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class CreateGameStateEvent extends Event {
    private final int numberOfPlayers;

    public CreateGameStateEvent(int numberOfPlayers, UUID parentEventID) {
        super("createNewGameStateHandler", parentEventID);
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
