package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class KOMsg extends SugarMessage {
    public final String reason;

    public KOMsg(String reason) {
        super(SugarMethod.CONTROL);
        this.reason = reason;
    }
}
