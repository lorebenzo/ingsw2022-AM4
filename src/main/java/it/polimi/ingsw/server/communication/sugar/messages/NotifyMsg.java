package it.polimi.ingsw.server.communication.sugar.messages;

import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class NotifyMsg extends Message {
    public final String data;

    public NotifyMsg(String data) {
        super(MessageType.NOTIFY);
        this.data = data;
    }

    public static NotifyMsg deserialize(String data) {
        return gson.fromJson(data, NotifyMsg.class);
    }
}
