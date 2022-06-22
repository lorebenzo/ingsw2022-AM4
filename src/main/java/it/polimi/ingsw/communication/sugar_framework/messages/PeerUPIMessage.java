package it.polimi.ingsw.communication.sugar_framework.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

import java.util.UUID;

public class PeerUPIMessage extends SugarMessage {
    public final UUID upi;

    public PeerUPIMessage(UUID upi) {
        super(SugarMethod.CONTROL);
        this.upi = upi;
    }
}
