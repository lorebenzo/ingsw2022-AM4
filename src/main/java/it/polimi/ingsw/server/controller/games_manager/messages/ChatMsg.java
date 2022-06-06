package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class ChatMsg extends SugarMessage {
    public final String from;
    public final String to;
    public final String message;

    // Used from the client, it needs the jwt for authenticating the client
    public ChatMsg(String from, String to, String message, String jwt) {
        super(SugarMethod.NOTIFY, jwt);
        this.from = from;
        this.to = to;
        this.message = message;
    }

    // Used from the server, sets a null the jwt, the client does not check che integrity of the server
    public ChatMsg(String from, String to, String message) {
        this(from, to, message, null);
    }
}
