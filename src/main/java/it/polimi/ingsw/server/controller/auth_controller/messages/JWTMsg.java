package it.polimi.ingsw.server.controller.auth_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class JWTMsg extends SugarMessage {
    public final String jwt;

    public JWTMsg(String jwt) {
        super(SugarMethod.JOIN);
        this.jwt = jwt;
    }
}
