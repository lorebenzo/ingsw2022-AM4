package it.polimi.ingsw.communication.sugar_framework;

import it.polimi.ingsw.communication.sugar_framework.exceptions.MessageDeserializationException;
import it.polimi.ingsw.communication.sugar_framework.exceptions.RoomNotFoundException;
import it.polimi.ingsw.communication.sugar_framework.messages.HeartBeatMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.PeerUPIMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.communication.tcp_server.TcpServer;
import it.polimi.ingsw.utils.multilist.MultiList;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class SugarServer extends TcpServer {
    private final static int                        port = 33400;

    protected final static UUID                     hallId = new UUID(0,0);
    private final Room                              hall;
    private final Set<Room>                         rooms = new HashSet<>();

    private final MultiList<UUID, Instant, Peer>    heartbeatsToWait = new MultiList<>();

    public SugarServer() throws IOException {
        super(port);

        // Set custom log header
        this.setLogHeader("[Sugar] : ");

        // Create hall
        this.hall = new Room(hallId);
        this.rooms.add(this.hall);
    }

    @Override
    protected void beforeStart() {
        this.startHeartbeatDaemon();
    }

    @Override
    protected final void onConnect(Socket client) {
        super.onConnect(client);

        var peer = new Peer(client);  // create the new peer
        synchronized (this.rooms) {
            this.hall.addPeer(peer);  // add it to the hall
        }
        this.log("New peer connected " + peer);
        try {
            this.sendUPI(peer);  // send it its upi
            this.log("UPI successfully sent to the new peer");
        } catch (IOException e) {
            log("Message could not be sent to the peer, delivery failed");
            this.disconnectPeer(peer);  // disconnect the peer
        }

        this.onPeerConnect(peer);
    }

    /**
     * The server calls this function any time a peer connects to the server
     * @param peer the peer that has connected
     */
    protected abstract void onPeerConnect(Peer peer);

    @Override
    protected final void onDisconnect(Socket client) {
        super.onDisconnect(client);

        var peer = this.getPeerFromClient(client);

        // Log the disconnection
        this.log("Peer disconnected: " + peer);

        // Remove the peer from any room
        synchronized (this.rooms) {
            if (peer.isPresent())
                for (var room : this.rooms) {
                    room.remove(peer.get());
                }
        }

        peer.ifPresent(this::onPeerDisconnect);
    }

    /**
     * The server calls this function any time a peer disconnects from the server
     * @param peer the peer that disconnected from the server
     */
    protected abstract void onPeerDisconnect(Peer peer);

    @Override
    protected final void onMessage(Socket client, String message) {
        super.onMessage(client, message);
        try {
            var msg = SugarMessage.deserialize(message);
            // If msg is a heartbeat, send it to the heartbeat handler
            if(msg.sugarMethod.equals(SugarMethod.HEARTBEAT))
                this.heartbeatHandler(msg);
                // Otherwise, send it to the normal message handler
            else {
                // Find the peer who sent the message
                var peer = this.getPeerFromClient(client);
                if(peer.isPresent())
                    this.onPeerMessage(peer.get(), msg);
                else
                    this.log("Received a message, but the peer does not exist, dropping message\n\t" + message);
            }
        } catch (MessageDeserializationException e) {
            this.log("Message deserialization error, dropping message:\n\t" + message);
        }
    }

    /**
     * The server calls this function any time it receives a message from a peer.
     * @param peer the peer who sent the message
     * @param message the message received from the peer
     */
    protected abstract void onPeerMessage(Peer peer, SugarMessage message);


    /* ************************ Heartbeat System *************************** */

    /**
     * Removes the message with that id from the heartbeatsToWait list
     * @param message the message received
     */
    void heartbeatHandler(SugarMessage message) {
        synchronized (this.heartbeatsToWait) {
            this.heartbeatsToWait.remove(message.messageID);
        }
    }

    /**
     * This function is responsible for the heartbeat system
     */
    private void startHeartbeatDaemon() {
        final int heartBeatPeriod = 5;  // seconds
        final int heartBeatTimeoutCheck = 5; // seconds

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Send heartbeats each heartbeatPeriod
        scheduler.scheduleAtFixedRate(this::sendHeartBeats, 0, heartBeatPeriod, TimeUnit.SECONDS);

        // Check heartbeat's timeout expiration each heartbeatTimeoutCheck
        scheduler.scheduleAtFixedRate(this::checkHeartBeatExpiration, 0, heartBeatTimeoutCheck, TimeUnit.SECONDS);
    }

    /**
     * Sends heartbeat messages to all peers
     */
    private void sendHeartBeats() {
        // Periodically send heartBeat messages
        Set<Peer> peers = new HashSet<>();
        synchronized (this.rooms) {
            for (var room : this.rooms)
                peers.addAll(room.getPeers());
        }

        peers.stream().parallel().forEach(peer -> {
            UUID msgId = UUID.randomUUID();
            SugarMessage msg = new HeartBeatMessage(msgId);
            try {
                this.send(msg, peer);
                synchronized (this.heartbeatsToWait) {
                    this.heartbeatsToWait.add(
                            msgId,
                            Instant.now(),
                            peer
                    );
                }
            } catch (IOException e) {
                log("Message could not be sent to the peer, delivery failed");
                this.disconnectPeer(peer);
            }
        });
    }

    /**
     * Identifies expired heartbeats
     */
    private void checkHeartBeatExpiration() {
        final int heartBeatTimeout = 5;  // seconds
        synchronized (this.heartbeatsToWait) {
            // Get expired MultiList
            var expired = this.heartbeatsToWait.filter(
                    (uuid, instant, peer) -> Duration.between(instant, Instant.now()).toSeconds() > heartBeatTimeout
            );

            // For each expired socket close connection and remove corresponding peer from any room
            expired.forEach(
                    (uuid, instant, peer) -> {
                        this.heartbeatsToWait.remove(uuid);
                        disconnectPeer(peer);
                    }
            );
        }
    }


    /* *************************** Rooms Handling ****************************** */

    /**
     * Disconnects the peer from the server
     * @param peer any connected peer
     */
    protected void disconnectPeer(Peer peer) {
        try {
            this.disconnectClient(peer.peerSocket);
        } catch (IOException ignored) { }
    }

    /**
     * Creates a new room
     * @return the UUID of the new room
     */
    protected UUID createRoom() {
        var room = new Room();
        synchronized (this.rooms) {
            this.rooms.add(room);
            return room.getRoomId();
        }
    }

    /**
     * Sends the message to any peer in the specified room (if it exists)
     * @param roomId uuid of the room
     * @param message message to send
     */
    protected void multicastToRoom(UUID roomId, SugarMessage message) throws IOException, RoomNotFoundException {
        synchronized (this.rooms) {
            // Find the room with the given roomId
            var room = this.rooms.stream().parallel()
                    .filter(room1 -> room1.getRoomId().equals(roomId))
                    .findFirst();

            // If the room exists, send the message to all peers in the room
            if(room.isPresent()) {
                for(var peer : room.get().getPeers()) {
                    this.send(message, peer);
                }
            } else {
                // Otherwise, throw RoomNotFoundException
                throw new RoomNotFoundException();
            }
        }
    }

    /**
     * Deletes the room with the specified id (hall cannot be deleted), and puts all the peers in that room back into the hall
     * @param roomId the id of the room to be deleted
     */
    protected void deleteRoom(UUID roomId) {
        if(!roomId.equals(hallId)) {
            this.rooms.stream()
                    .filter(_room -> _room.getRoomId().equals(roomId))  // find the room with the specified id
                    .findFirst()  // get the first occurrence
                    .ifPresent(_room -> {
                        synchronized (this.rooms) {
                            // Put all peers into the hall
                            this.hall.addPeers(_room.getPeers());
                            // Delete the room
                            this.rooms.remove(_room);
                        }
                    });
        }
    }


    /* ***************************** Message Delivery ***************************** */

    /**
     * Delivers the message to the peer
     * @param msg any Sugar message
     * @param peer any peer
     * @throws IOException if the message could not be sent through the socket
     */
    protected void send(SugarMessage msg, Peer peer) throws IOException {
        this.send(msg.serialize(), peer.peerSocket);
    }

    /**
     * Send the UPI initial message to the peer
     * @param peer any peer
     * @throws IOException if the message could not be sent through the socket
     */
    private void sendUPI(Peer peer) throws IOException {
        this.send(new PeerUPIMessage(peer.upi), peer);
    }


    /* ****************************** Helper Functions *****************************/

    /**
     *
     * @param client any connected client
     * @return the peer associated to that client
     */
    private Optional<Peer> getPeerFromClient(Socket client) {
        synchronized (this.rooms) {
            return this.rooms.stream()
                    .flatMap(room -> room.getPeers().stream())
                    .filter(peer -> peer.peerSocket.equals(client))
                    .findFirst();
        }
    }
}
