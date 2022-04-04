package it.polimi.ingsw.server.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class InitializeSchoolBoardsAndCloudsEvent extends Event {
    public final int numberOfPlayers;
    public InitializeSchoolBoardsAndCloudsEvent(int numberOfPlayers, UUID parentEvent) {
        super("initializeSchoolBoardAndCloudsEvent", parentEvent);
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
