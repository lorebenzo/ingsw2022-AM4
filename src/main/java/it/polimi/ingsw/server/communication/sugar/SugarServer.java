package it.polimi.ingsw.server.communication.sugar;

import it.polimi.ingsw.server.communication.tcp_server.Server;
import it.polimi.ingsw.server.communication.exceptions.ServerCreationException;
import it.polimi.ingsw.server.communication.sugar.messages.*;
import it.polimi.ingsw.utils.multilist.MultiList;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SugarServer implements Runnable {
    private final Server                            sugarServer;
    private final static int                        port = 33400;

    protected final static UUID                     hallId = new UUID(0,0);
    protected final List<Room>                      rooms = new LinkedList<>();

    private final MultiList<UUID, Instant, Peer>    heartbeatsToWait = new MultiList<>();

    public SugarServer() throws ServerCreationException {
        // Create hall room
        rooms.add(new Room(hallId));

        this.sugarServer = new Server(port)
                // Tell the server to add a client to the hall whenever it connects to this server
                .onConnect((client) -> Objects.requireNonNull(getHall()).addPeer(new Peer(client)))
                // Tell the server to forward any message received to the handleMessage function
                .onMessage(this::handleMessage);

        // Start heartbeat daemon
        this.heartBeatDaemon();
    }

    @Override
    public void run() {
        this.sugarServer.run();
    }

    /**
     * Calls the correct handler basing on the type of message received
     * @param client any client socket
     * @param message any message string
     */
    private void handleMessage(Socket client, String message) {
        Message msg = Message.deserialize(message);
        switch (msg.messageType) {
            case JOIN:
                joinHandler(client, JoinMsg.deserialize(message));
                break;
            case CONTROL:
                controlHandler(client, ControlMsg.deserialize(message));
                break;
            case ACTION:
                actionHandler(client, ActionMsg.deserialize(message));
                break;
            case NOTIFY:
                notifyHandler(client, NotifyMsg.deserialize(message));
                break;
            case HEARTBEAT:
                heartbeatHandler(client, HeartbeatMsg.deserialize(message));
                break;
        }
    }

    protected void joinHandler(Socket client, JoinMsg msg) {}
    protected void controlHandler(Socket client, ControlMsg msg) {}
    protected void actionHandler(Socket client, ActionMsg msg) {}
    protected void notifyHandler(Socket client, NotifyMsg msg) {}

    private void heartbeatHandler(Socket client, HeartbeatMsg msg) {
        synchronized (this.heartbeatsToWait) {
            this.heartbeatsToWait.remove(msg.messageID);
        }
    }

    /**
     * This function is responsible for the heartbeat system
     */
    private void heartBeatDaemon() {
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
            HeartbeatMsg msg = new HeartbeatMsg(msgId);
            if(this.send(msg, peer))
                synchronized (this.heartbeatsToWait) {
                    this.heartbeatsToWait.add(
                            msgId,
                            Instant.now(),
                            peer
                    );
                }
            else {
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

    /**
     *
     * @return the hall, as specified in the Sugar protocol
     */
    protected Room getHall() {
        synchronized (this.rooms) {
            for(var room : this.rooms)
                if(this.isHall(room)) return room;
        }
        return null;
    }

    /**
     *
     * @param room any room
     * @return true if the room is the hall, false otherwise
     */
    protected boolean isHall(Room room) {
        return room.getRoomId().equals(hallId);
    }

    /**
     * Delivers the message to the peer
     * @param msg any Sugar message
     * @param peer any peer
     * @return true if the message was sent successfully, false otherwise
     */
    protected boolean send(Message msg, Peer peer) {
        try {
            this.sugarServer.send(msg.serialize(), peer.peerSocket);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Disconnects the peer from the server
     * @param peer any connected peer
     */
    protected void disconnectPeer(Peer peer) {
        for(var room : this.rooms) {
            room.remove(peer);
            try {
                this.sugarServer.disconnectClient(peer.peerSocket);
            } catch (IOException ignored) { }
        }
    }

    /**
     * Creates a new room
     * @return the UUID of the new room
     */
    protected UUID createRoom() {
        var room = new Room();
        this.rooms.add(room);
        return room.getRoomId();
    }
}
