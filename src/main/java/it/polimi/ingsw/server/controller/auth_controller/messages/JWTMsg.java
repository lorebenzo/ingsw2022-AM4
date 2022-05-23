package it.polimi.ingsw.server.controller.auth_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class JWTMsg extends SugarMessage {
    public final String jwtAuthCode;

    public JWTMsg(String jwtAuthCode) {
        super(SugarMethod.CONTROL);
        this.jwtAuthCode = jwtAuthCode;
    }
}
