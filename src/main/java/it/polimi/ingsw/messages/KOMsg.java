package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class KOMsg extends SugarMessage {
    String reason;

    public KOMsg(String reason) {
        super(SugarMethod.CONTROL);
        this.reason = reason;

    }
}
