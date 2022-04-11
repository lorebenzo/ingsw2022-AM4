package it.polimi.ingsw.server.communication.sugar.messages;

import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class JoinMsg extends Message {
    public JoinMsg() {
        super(MessageType.JOIN);
    }

    public JoinMsg(UUID messageID) {
        super(MessageType.JOIN, messageID);
    }

    public static JoinMsg deserialize(String data) {
        return gson.fromJson(data, JoinMsg.class);
    }
}
