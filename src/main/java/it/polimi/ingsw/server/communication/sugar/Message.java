package it.polimi.ingsw.server.communication.sugar;

import com.google.gson.Gson;
import it.polimi.ingsw.server.communication.sugar.exceptions.MessageSerializationException;
import it.polimi.ingsw.server.communication.sugar.exceptions.MessageDeserializationException;

import java.util.UUID;

public class Message {
    protected static Gson gson = new Gson();

    public final Class<?> messageClass;
    public final MessageType messageType;
    public final UUID messageID; // Useful to identify which message is a response to another message

    public Message(MessageType messageType, UUID messageID) {
        this.messageClass = this.getClass();
        this.messageType = messageType;
        this.messageID = messageID;
    }

    public Message(MessageType messageType) {
        this(messageType, UUID.randomUUID());
    }

    public final String serialize() throws MessageSerializationException {
        return SerDes.serialize(this);
    }

    /**
     * @param message json-encoded message to deserialize
     * @return the deserialized message
     * @throws MessageDeserializationException if this method failed to deserialize the message
     */
    public static Message deserialize(String message) throws MessageDeserializationException {
        return SerDes.deserialize(message);
    }
}
