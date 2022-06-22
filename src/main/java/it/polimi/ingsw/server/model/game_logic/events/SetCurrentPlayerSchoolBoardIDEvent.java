package it.polimi.ingsw.server.model.game_logic.events;

import it.polimi.ingsw.server.event_sourcing.Event;

import java.util.UUID;

public class SetCurrentPlayerSchoolBoardIDEvent extends Event {
    public final int schoolBoardID;

    public SetCurrentPlayerSchoolBoardIDEvent(UUID parentEvent, int schoolBoardID) {
        super("setCurrentPlayerSchoolBoardIdHandler", parentEvent);
        this.schoolBoardID = schoolBoardID;
    }
}
