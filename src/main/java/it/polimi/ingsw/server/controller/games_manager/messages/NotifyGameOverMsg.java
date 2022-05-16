package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class NotifyGameOverMsg extends SugarMessage {
    public final String text;

    public NotifyGameOverMsg(String text) {
        super(SugarMethod.CONTROL);
        this.text = text;
    }
}
