package it.polimi.ingsw.server.communication.sugar;

import java.util.*;

/**
 * Represents a room in the Sugar communication protocol
 */
public class Room {
    private final UUID roomId;  // Unique identifier for this room
    private final Set<Peer> peers;  // Set of peers that joined this room

    public Room() {
        this.roomId = UUID.randomUUID();
        this.peers = new HashSet<>();
    }

    public Room(UUID roomId) {
        this.roomId = roomId;
        this.peers = new HashSet<>();
    }

    /**
     * Makes the peer join into this room
     *
     * @param peer any peer connected to the server
     */
    public void addPeer(Peer peer) {
        this.peers.add(peer);
    }


    public UUID getRoomId() {
        return roomId;
    }

    public Set<Peer> getPeers() {
        return new HashSet<>(peers);
    }

    /**
     * If the peer is in this room, makes the peer leave the room, otherwise it has no effect
     * @param peer any peer
     */
    public void remove(Peer peer) {
        this.peers.remove(peer);
    }
}
