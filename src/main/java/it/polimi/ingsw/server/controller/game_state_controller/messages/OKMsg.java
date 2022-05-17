package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class OKMsg extends SugarMessage {
    public final String text;

    public OKMsg() {
        super(SugarMethod.CONTROL);
        this.text = "OK";
    }

    public OKMsg(String text) {
        super(SugarMethod.CONTROL);
        this.text = text;

    }

}