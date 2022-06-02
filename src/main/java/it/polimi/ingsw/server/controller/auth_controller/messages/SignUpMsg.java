package it.polimi.ingsw.server.controller.auth_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class SignUpMsg extends SugarMessage {
    public final String username;
    public final String password;

    public SignUpMsg(String username, String password) {
        super(SugarMethod.JOIN);
        this.username = username;
        this.password = password;
    }
}
