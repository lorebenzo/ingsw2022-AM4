package it.polimi.ingsw.server.controller.auth_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class LoginMsg extends SugarMessage {
    public final String username;
    public final String password;

    public LoginMsg(String username, String password) {
        super(SugarMethod.JOIN);
        this.username = username;
        this.password = password;
    }
}
