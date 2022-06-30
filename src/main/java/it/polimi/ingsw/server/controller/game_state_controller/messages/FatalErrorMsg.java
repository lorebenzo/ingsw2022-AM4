package it.polimi.ingsw.server.controller.game_state_controller.messages;

public class FatalErrorMsg extends KOMsg{

    public FatalErrorMsg(String reason) {
        super(reason);
    }
}
