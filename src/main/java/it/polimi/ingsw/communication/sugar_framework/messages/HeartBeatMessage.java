package it.polimi.ingsw.communication.sugar_framework.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

import java.util.UUID;

public class HeartBeatMessage extends SugarMessage {
    public HeartBeatMessage(UUID messageID) {
        super(SugarMethod.HEARTBEAT, messageID, null);
    }
}
