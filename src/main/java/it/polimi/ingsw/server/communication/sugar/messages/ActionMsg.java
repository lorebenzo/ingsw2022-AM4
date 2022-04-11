package it.polimi.ingsw.server.communication.sugar.messages;

import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class ActionMsg extends Message {
    public ActionMsg() {
        super(MessageType.ACTION);
    }

    public ActionMsg(UUID messageID) {
        super(MessageType.ACTION, messageID);
    }

    public static ActionMsg deserialize(String data) {
        return gson.fromJson(data, ActionMsg.class);
    }
}