package it.polimi.ingsw.server.communication.sugar.messages;

import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class ControlMsg extends Message {
    public ControlMsg() {
        super(MessageType.CONTROL);
    }

    public ControlMsg(UUID messageID) {
        super(MessageType.CONTROL, messageID);
    }

    public static ControlMsg deserialize(String data) {
        return gson.fromJson(data, ControlMsg.class);
    }
}
