package it.polimi.ingsw.communication.sugar_framework.messages;

import it.polimi.ingsw.communication.sugar_framework.SerDes;
import it.polimi.ingsw.communication.sugar_framework.exceptions.MessageDeserializationException;

import java.util.UUID;

/**
 * A Sugar Message, all Sugar messages extend from this base class
 */
public abstract class SugarMessage {
    /* ****************** Header *********************** */
    public final Class<?> messageClass;
    public final SugarMethod sugarMethod;
    public final UUID messageID; // Useful to identify which message is a response to another message
    /* ************************************************* */

    public SugarMessage(SugarMethod sugarMethod, UUID messageID) {
        this.messageClass = this.getClass();
        this.sugarMethod = sugarMethod;
        this.messageID = messageID;
    }

    public SugarMessage(SugarMethod sugarMethod) {
        this(sugarMethod, UUID.randomUUID());
    }

    public final String serialize() {
        return SerDes.serialize(this);
    }

    /**
     * @param message json-encoded message to deserialize
     * @return the deserialized message
     * @throws MessageDeserializationException if this method failed to deserialize the message
     */
    public static SugarMessage deserialize(String message) throws MessageDeserializationException {
        return SerDes.deserialize(message);
    }
}
