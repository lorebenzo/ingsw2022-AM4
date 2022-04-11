package it.polimi.ingsw.server.communication.sugar.messages;

import com.google.gson.Gson;
import it.polimi.ingsw.server.communication.sugar.MessageType;

import java.util.UUID;

public class Message {
    protected static Gson gson = new Gson();

    public final MessageType messageType;
    public final UUID messageID; // Useful to identify which message is a response to another message

    public Message(MessageType messageType) {
        this(messageType, UUID.randomUUID());
    }

    public Message(MessageType messageType, UUID messageID) {
        this.messageType = messageType;
        this.messageID = messageID;
    }

    public String serialize() {
        return gson.toJson(this)
                .replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                + "\n";  // Adds a newline to the end
    }

    public static Message deserialize(String data) {
        return gson.fromJson(data, Message.class);
    }
}
