package it.polimi.ingsw.server.communication.sugar;

import java.util.UUID;

/**
 * Message used to communicate to the peer its UPI
 */
public class PeerUPIMessage extends Message {
    public UUID upi;  // The upi of the peer

    public PeerUPIMessage(UUID upi) {
        super(MessageType.CONTROL);
        this.upi = upi;
    }
}
