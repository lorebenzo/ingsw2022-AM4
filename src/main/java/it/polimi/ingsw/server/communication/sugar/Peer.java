package it.polimi.ingsw.server.communication.sugar;

import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a peer in the Sugar communication protocol
 */
public class Peer {
    public final UUID upi;  // Unique Peer Identifier - identifies a peer in a session
    public final Socket peerSocket;  // The peer's socket in this session

    public Peer(Socket peerSocket) {
        this.upi = UUID.randomUUID();
        this.peerSocket = peerSocket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return upi.equals(peer.upi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upi);
    }
}
