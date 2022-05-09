package it.polimi.ingsw.server.controller.game_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class ChatToMsg extends SugarMessage {
    public final String msg;
    public final String to; // Username of the receiver

    public ChatToMsg(String msg, String to) {
        super(SugarMethod.NOTIFY);
        this.msg = msg;
        this.to = to;
    }
}
