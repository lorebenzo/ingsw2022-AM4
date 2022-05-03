package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class OKMsg extends SugarMessage {
    String text;

    public OKMsg() {
        super(SugarMethod.CONTROL);
    }


    public OKMsg(String text) {
        super(SugarMethod.CONTROL);
        this.text = text;

    }

}