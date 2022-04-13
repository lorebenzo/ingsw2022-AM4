package it.polimi.ingsw.server.communication.sugar;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a room in the Sugar communication protocol
 */
public class Room {
    private final UUID roomId;  // Unique identifier for this room
    private final Set<Peer> peers = new CopyOnWriteArraySet<>();  // thread-safe set of peers that joined this room

    public Room() {
        this.roomId = UUID.randomUUID();
    }

    public Room(UUID roomId) {
        this.roomId = roomId;
    }

    /**
     * Makes the peer join into this room
     *
     * @param peer any peer connected to the server
     */
    public void addPeer(Peer peer) {
        this.peers.add(peer);
    }

    /**
     * Adds all peers to the room
     * @param peers a collection of Peer(s)
     */
    public void addPeers(Collection<Peer> peers) {
        this.peers.addAll(peers);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
