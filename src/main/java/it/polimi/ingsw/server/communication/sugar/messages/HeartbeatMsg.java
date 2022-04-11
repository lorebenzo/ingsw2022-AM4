package it.polimi.ingsw.server.communication.sugar.messages;

import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class HeartbeatMsg extends Message {
    public HeartbeatMsg() {
        super(MessageType.HEARTBEAT);
    }

    public HeartbeatMsg(UUID messageID) {
        super(MessageType.HEARTBEAT, messageID);
    }

    public static HeartbeatMsg deserialize(String data) {
        return gson.fromJson(data, HeartbeatMsg.class);
    }
}
